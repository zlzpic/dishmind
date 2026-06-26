package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.entity.Recipe;
import com.bdu.dishmind.entity.RecipeTag;
import com.bdu.dishmind.entity.User;
import com.bdu.dishmind.entity.UserTagPreference;
import com.bdu.dishmind.repository.*;
import com.bdu.dishmind.service.RecommendService;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private UserTagPreferenceRepository userTagPreferenceRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeTagRepository recipeTagRepository;
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RecipeResponse> forYou(Long userId, Integer limit) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        if (limit == null || limit < 1) limit = 10;

        // 1. 获取用户Top5偏好标签（含完整信息）
        List<UserTagPreference> topPrefs = userTagPreferenceRepository
                .findTopByUserIdOrderByScoreDesc(userId, PageRequest.of(0, 5));

        // 无历史：返回热门
        if (topPrefs.isEmpty()) {
            Page<Recipe> hotPage = recipeRepository.findByStatus(2,
                    PageRequest.of(0, limit, Sort.by("viewCount").descending()));
            return convertToResponseList(hotPage.getContent());
        }

        // 2. 计算每个标签的置信度和加权分数
        List<Integer> tagIds = new ArrayList<>();
        Map<Integer, Double> tagConfidenceMap = new HashMap<>();
        Map<Integer, BigDecimal> tagScoreMap = new HashMap<>();

        for (UserTagPreference pref : topPrefs) {
            tagIds.add(pref.getTagId());

            // 计算置信度：positive / (positive + negative + 1)
            int positive = pref.getPositiveCount() != null ? pref.getPositiveCount() : 0;
            int negative = pref.getNegativeCount() != null ? pref.getNegativeCount() : 0;
            double confidence = (double) positive / (positive + negative + 1);

            tagConfidenceMap.put(pref.getTagId(), confidence);
            tagScoreMap.put(pref.getTagId(), pref.getScore());
        }

        // 3. 获取已看过的菜谱ID（排除）
        List<Long> viewedIds = userBehaviorRepository.findRecipeIdsByUserId(userId);
        if (viewedIds.isEmpty()) {
            viewedIds.add(0L); // 防止SQL空列表错误
        }

        // 4. 查询候选菜谱（扩大3倍候选集用于精排）
        List<Recipe> candidates = recipeRepository.findByTagIdsAndExcludeIds(
                tagIds, viewedIds, limit * 3);

        if (candidates.isEmpty()) {
            // 无匹配，返回热门
            Page<Recipe> hotPage = recipeRepository.findByStatus(2,
                    PageRequest.of(0, limit, Sort.by("viewCount").descending()));
            return convertToResponseList(hotPage.getContent());
        }

        // 5. 在内存中按置信度加权重新排序
        List<RecipeWithFinalScore> scoredRecipes = new ArrayList<>();

        for (Recipe recipe : candidates) {
            // 查该菜谱的标签关联
            List<RecipeTag> recipeTags = recipeTagRepository.findByRecipeId(recipe.getId());

            double finalScore = 0.0;

            // 累加每个匹配标签的加权分数：recipe_tag.weight × confidence
            for (RecipeTag rt : recipeTags) {
                if (tagConfidenceMap.containsKey(rt.getTagId())) {
                    double confidence = tagConfidenceMap.get(rt.getTagId());
                    double weight = rt.getWeight() != null ? rt.getWeight().doubleValue() : 1.0;

                    // 核心公式：权重 × 置信度
                    finalScore += weight * confidence;
                }
            }

            scoredRecipes.add(new RecipeWithFinalScore(recipe, finalScore));
        }

        // 6. 按最终分数降序排列，取TopN
        scoredRecipes.sort((a, b) -> Double.compare(b.finalScore, a.finalScore));

        List<Recipe> result = scoredRecipes.stream()
                .limit(limit)
                .map(rs -> rs.recipe)
                .collect(Collectors.toList());

        return convertToResponseList(result);
    }

    @Override
    public PageResult<RecipeResponse> hot(Integer page, Integer size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PageRequest pageable = PageRequest.of(page - 1, size,
                Sort.by("rating").descending().and(Sort.by("viewCount").descending()));

        Page<Recipe> recipePage = recipeRepository.findByStatus(2, pageable);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipePage.getContent()) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, recipePage.getTotalElements(), page, size);
    }

    @Override
    public PageResult<RecipeResponse> latest(Integer page, Integer size) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage = recipeRepository.findByStatus(2, pageable);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipePage.getContent()) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, recipePage.getTotalElements(), page, size);
    }

    // 内部类：带最终分数的菜谱
    private static class RecipeWithFinalScore {
        Recipe recipe;
        double finalScore;

        RecipeWithFinalScore(Recipe recipe, double finalScore) {
            this.recipe = recipe;
            this.finalScore = finalScore;
        }
    }

    // 转换方法
    private List<RecipeResponse> convertToResponseList(List<Recipe> recipes) {
        List<RecipeResponse> result = new ArrayList<>();
        for (Recipe recipe : recipes) {
            result.add(convertToResponse(recipe));
        }
        return result;
    }

    private RecipeResponse convertToResponse(Recipe recipe) {
        RecipeResponse dto = new RecipeResponse();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setCoverImage(recipe.getCoverImage());
        dto.setAuthorId(recipe.getAuthorId());
        dto.setDifficulty(recipe.getDifficulty());
        dto.setCookTime(recipe.getCookTime());
        dto.setRating(recipe.getRating());
        dto.setRatingCount(recipe.getRatingCount());
        dto.setViewCount(recipe.getViewCount());
        dto.setCollectCount(recipe.getCollectCount());
        dto.setStatus(recipe.getStatus());
        dto.setCreatedAt(recipe.getCreatedAt());
        User author = userRepository.findById(recipe.getAuthorId()).orElse(null);
        dto.setAuthorNickname(author != null ? author.getNickname() : "未知用户");
        return dto;
    }
}

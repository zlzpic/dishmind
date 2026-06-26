package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.*;
import com.bdu.dishmind.dto.response.RecipeDetailResponse;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.dto.response.RecipeStepResponse;
import com.bdu.dishmind.dto.response.TagResponse;
import com.bdu.dishmind.entity.*;
import com.bdu.dishmind.repository.*;
import com.bdu.dishmind.service.BehaviorService;
import com.bdu.dishmind.service.RecipeService;
import com.bdu.dishmind.utils.BizException;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagCategoryRepository tagCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private BehaviorService behaviorService;
    @Autowired
    private RecipeTagRepository recipeTagRepository;

    @Autowired
    private RecipeStepRepository recipeStepRepository;

    @Override
    public PageResult<RecipeResponse> list(Integer page, Integer size, String sort) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PageRequest pageable;
        if ("latest".equals(sort)) {
            pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        } else {
            pageable = PageRequest.of(page - 1, size,
                    Sort.by("viewCount").descending().and(Sort.by("collectCount").descending()));
        }

        Page<Recipe> recipePage = recipeRepository.findByStatus(2, pageable);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipePage.getContent()) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, recipePage.getTotalElements(), page, size);
    }
    @Override
    public PageResult<RecipeResponse> listByStatus(Integer status, Integer page, Integer size, String sort) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PageRequest pageable;
        if ("latest".equals(sort)) {
            pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        } else {
            pageable = PageRequest.of(page - 1, size,
                    Sort.by("viewCount").descending().and(Sort.by("collectCount").descending()));
        }
        Page<Recipe> recipePage = recipeRepository.findByStatus(status, pageable);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipePage.getContent()) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, recipePage.getTotalElements(), page, size);
    }

    @Override
    public PageResult<RecipeResponse> listMy(Long userId, Integer page, Integer size) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage = recipeRepository.findByAuthorId(userId, pageable);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipePage.getContent()) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, recipePage.getTotalElements(), page, size);
    }

    @Override
    public PageResult<RecipeResponse> search(RecipeSearchRequest request) {
        if (request.getPage() == null || request.getPage() < 1) request.setPage(1);
        if (request.getSize() == null || request.getSize() < 1) request.setSize(10);
        if (ServiceUtil.isEmpty(request.getKeyword())) {
            throw new BizException("关键词不能为空");
        }

        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getSize(),
                Sort.by("viewCount").descending());

        Page<Recipe> recipePage = recipeRepository
                .findByStatusAndTitleContaining(2, request.getKeyword(), pageable);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipePage.getContent()) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, recipePage.getTotalElements(), request.getPage(), request.getSize());
    }

    @Override
    public PageResult<RecipeResponse> filterByTag(RecipeFilterRequest request) {
        ServiceUtil.requireNonNull(request.getTagId(), "标签ID不能为空");
        if (request.getPage() == null || request.getPage() < 1) request.setPage(1);
        if (request.getSize() == null || request.getSize() < 1) request.setSize(10);

        int offset = ServiceUtil.calculateOffset(request.getPage(), request.getSize());

        List<Recipe> recipes = recipeRepository.findByTagId(
                request.getTagId(), offset, request.getSize());

        Long total = recipeRepository.countByTagId(request.getTagId());

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, total, request.getPage(), request.getSize());
    }

    @Override
    public PageResult<RecipeResponse> filterByTags(List<Integer> tagIds, Integer page, Integer size) {
        ServiceUtil.requireNonNull(tagIds, "标签ID不能为空");
        if (tagIds.isEmpty()) {
            throw new BizException("至少选择一个标签");
        }
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = ServiceUtil.calculateOffset(page, size);

        // 使用且关系查询（必须同时包含所有标签）
        List<Recipe> recipes = recipeRepository.findByTagIdsAllMatch(tagIds, tagIds.size(), offset, size);
        Long total = recipeRepository.countByTagIdsAllMatch(tagIds, tagIds.size());

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            dtoList.add(convertToResponse(recipe));
        }

        return PageResult.success(dtoList, total, page, size);
    }

    @Override
    public RecipeDetailResponse getDetail(Long id, Long userId) {
        ServiceUtil.requireNonNull(id, "菜谱ID不能为空");
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new BizException("菜谱不存在"));

        if (recipe.getStatus() != 2
                && !recipe.getAuthorId().equals(userId)
                && !isAdmin(userId)) {
            throw new BizException("菜谱未公开");
        }

        RecipeDetailResponse dto = new RecipeDetailResponse();
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
        dto.setUpdatedAt(recipe.getUpdatedAt());

        User author = userRepository.findById(recipe.getAuthorId()).orElse(null);
        dto.setAuthorNickname(author != null ? author.getNickname() : "未知用户");
        dto.setAuthorAvatarUrl(author != null ? author.getAvatarUrl() : "default");

        // 查标签并补全categoryName和weight
        List<Tag> tags = tagRepository.findByRecipeId(id);
        List<TagResponse> tagResponses = new ArrayList<>();
        for (Tag tag : tags) {
            TagResponse tr = new TagResponse();
            tr.setId(tag.getId());
            tr.setName(tag.getName());
            tr.setCategoryId(tag.getCategoryId());

            // 补全categoryName
            TagCategory category = tagCategoryRepository.findById(tag.getCategoryId()).orElse(null);
            tr.setCategoryName(category != null ? category.getName() : "");

            // weight从usage_count取（或根据业务需要改为recipe_tag.weight）
            tr.setWeight(tag.getUsageCount() != null ? tag.getUsageCount().doubleValue() : 0.0);

            tagResponses.add(tr);
        }
        dto.setTags(tagResponses);

        // 新增：查制作步骤
        List<RecipeStep> steps = recipeStepRepository.findByRecipeIdAndStatusOrderByStepNumberAsc(id, 0);
        List<RecipeStepResponse> stepResponses = new ArrayList<>();
        int totalDuration = 0;
        int displayOrder = 1; // 新增：动态序号从1开始

        for (RecipeStep step : steps) {
            RecipeStepResponse sr = new RecipeStepResponse();
            sr.setId(step.getId());
            sr.setStepNumber(step.getStepNumber());
            sr.setDisplayOrder(displayOrder++);  // 新增：设置动态显示序号
            sr.setDescription(step.getDescription());
            sr.setImageUrl(step.getImageUrl());
            sr.setTip(step.getTip());
            sr.setDurationSeconds(step.getDurationSeconds());
            stepResponses.add(sr);

            if (step.getDurationSeconds() != null) {
                totalDuration += step.getDurationSeconds();
            }
        }

        dto.setSteps(stepResponses);
        dto.setTotalSteps(steps.size());
        dto.setTotalDuration(totalDuration);

        if (userId != null) {
            boolean isFavorited = userFavoriteRepository
                    .existsByUserIdAndRecipeIdAndStatus(userId, id, 0);
            dto.setIsFavorited(isFavorited);
        } else {
            dto.setIsFavorited(false);
        }

        if (userId != null) {
            BehaviorReportRequest reportRequest = new BehaviorReportRequest();
            reportRequest.setUserId(userId);
            reportRequest.setRecipeId(id);
            reportRequest.setBehaviorType("VIEW");
            reportRequest.setDurationSeconds(0);
            behaviorService.report(reportRequest);
        }

        return dto;
    }

    @Override
    public List<RecipeResponse> findSimilar(Long recipeId, Integer limit) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        if (limit == null || limit < 1) limit = 5;

        List<Recipe> recipes = recipeRepository.findSimilarByTags(recipeId, limit);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            dtoList.add(convertToResponse(recipe));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public RecipeResponse createRecipe(RecipeCreateRequest request, Long userId) {
        // 1. 参数校验
        ServiceUtil.requireNonNull(request.getTitle(), "菜谱标题不能为空");
        ServiceUtil.requireNonNull(request.getTagIds(), "至少选择一个标签");
        if (request.getTagIds().isEmpty()) {
            throw new BizException("至少选择一个标签");
        }

        // 2. 保存菜谱主表（状态0-草稿）
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setCoverImage(request.getCoverImage());
        recipe.setAuthorId(userId);
        recipe.setDifficulty(request.getDifficulty());
        recipe.setCookTime(request.getCookTime());
        recipe.setRating(new BigDecimal("5.0"));
        recipe.setRatingCount(0);
        recipe.setViewCount(0);
        recipe.setCollectCount(0);
        recipe.setStatus(0);  // 0-草稿，需审核
        Recipe savedRecipe = recipeRepository.save(recipe);

        // 3. 绑定标签（批量插入recipe_tag）
        for (Integer tagId : request.getTagIds()) {
            RecipeTag rt = new RecipeTag();
            rt.setRecipeId(savedRecipe.getId());
            rt.setTagId(tagId);
            rt.setWeight(new BigDecimal("1.0"));
            recipeTagRepository.save(rt);

            // 4. 更新标签使用次数
            Tag tag = tagRepository.findById(tagId).orElse(null);
            if (tag != null) {
                tag.setUsageCount(tag.getUsageCount() != null ? tag.getUsageCount() + 1 : 1);
                tagRepository.save(tag);
            }
        }

        // 5. 保存步骤（如果有）
        if (request.getSteps() != null && !request.getSteps().isEmpty()) {
            int stepNum = 1;
            for (RecipeStepRequest stepReq : request.getSteps()) {
                RecipeStep step = new RecipeStep();
                step.setRecipeId(savedRecipe.getId());
                step.setStepNumber(stepNum++);
                step.setDescription(stepReq.getDescription());
                step.setImageUrl(stepReq.getImageUrl());
                step.setTip(stepReq.getTip());
                step.setDurationSeconds(stepReq.getDurationSeconds());
                step.setStatus(0);  // 步骤也标记正常
                recipeStepRepository.save(step);
            }
        }

        return convertToResponse(savedRecipe);
    }

    @Override
    @Transactional
    public void republish(Long recipeId, Long userId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new BizException("菜谱不存在"));

        // 权限校验：作者或管理员
        if (!recipe.getAuthorId().equals(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BizException("用户不存在"));
            if (user.getUserType() == null || user.getUserType() != 1) {
                throw new BizException("无权限操作该菜谱");
            }
        }

        // 状态校验：只能是草稿(0)或已下架(3)才能重新提交
        if (recipe.getStatus() != 0 && recipe.getStatus() != 3) {
            throw new BizException("当前状态不允许重新上架");
        }

        // 修改：改为审核中状态(1)，而非直接正常显示(2)
        recipe.setStatus(1);
        recipeRepository.save(recipe);
    }

    @Override
    @Transactional
    public void offline(Long recipeId, Long userId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new BizException("菜谱不存在"));

        // 权限校验：作者或管理员
        if (!recipe.getAuthorId().equals(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BizException("用户不存在"));
            if (user.getUserType() == null || user.getUserType() != 1) {
                throw new BizException("无权限操作该菜谱");
            }
        }

        // 状态校验：只能下架正常显示(2)的菜谱
        if (recipe.getStatus() == 0||recipe.getStatus() == 3) {
            throw new BizException("当前状态不允许下架");
        }

        recipe.setStatus(3); // 改为下架
        recipeRepository.save(recipe);
    }


    @Override
    @Transactional
    public void approveRecipe(Long recipeId, Long adminId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(adminId, "管理员ID不能为空");

        // 校验管理员权限
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BizException("用户不存在"));
        if (admin.getUserType() == null || admin.getUserType() != 1) {
            throw new BizException("无权限，仅管理员可操作");
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new BizException("菜谱不存在"));

        // 只能审核审核中(1)状态
        if (recipe.getStatus() != 1) {
            throw new BizException("该菜谱不在审核中状态");
        }

        recipe.setStatus(2); // 改为正常显示
        recipeRepository.save(recipe);
    }

    @Override
    @Transactional
    public void rejectRecipe(Long recipeId, Long adminId, String reason) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(adminId, "管理员ID不能为空");

        // 校验管理员权限
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BizException("用户不存在"));
        if (admin.getUserType() == null || admin.getUserType() != 1) {
            throw new BizException("无权限，仅管理员可操作");
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new BizException("菜谱不存在"));

        // 只能驳回审核中(1)状态
        if (recipe.getStatus() != 1) {
            throw new BizException("该菜谱不在审核中状态");
        }

        recipe.setStatus(0); // 打回草稿
        // 记录驳回原因到描述末尾
        if (!ServiceUtil.isEmpty(reason)) {
            String original = recipe.getDescription() != null ? recipe.getDescription() : "";
            recipe.setDescription(original + "【驳回原因：" + reason + "】");
        }
        // reason 可记录到日志或扩展字段，目前只改状态
        recipeRepository.save(recipe);
    }

    // 在 RecipeServiceImpl 里加私有方法
    private boolean isAdmin(Long userId) {
        if (userId == null) return false;
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getUserType() != null && user.getUserType() == 1;
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

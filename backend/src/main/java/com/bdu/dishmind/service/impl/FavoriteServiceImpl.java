package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.BehaviorReportRequest;
import com.bdu.dishmind.dto.request.FavoriteFolderRenameRequest;
import com.bdu.dishmind.dto.request.FavoriteRequest;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.entity.Recipe;
import com.bdu.dishmind.entity.User;
import com.bdu.dishmind.entity.UserFavorite;
import com.bdu.dishmind.repository.RecipeRepository;
import com.bdu.dishmind.repository.UserFavoriteRepository;
import com.bdu.dishmind.repository.UserRepository;
import com.bdu.dishmind.service.BehaviorService;
import com.bdu.dishmind.service.FavoriteService;
import com.bdu.dishmind.utils.BizException;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private BehaviorService behaviorService;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void toggleFavorite(FavoriteRequest request) {
        ServiceUtil.requireNonNull(request.getUserId(), "用户ID不能为空");
        ServiceUtil.requireNonNull(request.getRecipeId(), "菜谱ID不能为空");

        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new BizException("菜谱不存在"));

        Optional<UserFavorite> exist = userFavoriteRepository
                .findByUserIdAndRecipeId(request.getUserId(), request.getRecipeId());

        String folderName = ServiceUtil.isEmpty(request.getFolderName()) ?
                "默认收藏夹" : request.getFolderName();

        if (exist.isPresent()) {
            UserFavorite favorite = exist.get();
            if (favorite.getStatus() == 0) {
                // 取消收藏：状态改为1，记录负向行为
                favorite.setStatus(1);
                favorite.setUpdatedAt(LocalDateTime.now());
                userFavoriteRepository.save(favorite);

                recipe.setCollectCount(recipe.getCollectCount() - 1);
                recipeRepository.save(recipe);

                // 上报取消收藏行为（负向）
                BehaviorReportRequest reportRequest = new BehaviorReportRequest();
                reportRequest.setUserId(request.getUserId());
                reportRequest.setRecipeId(request.getRecipeId());
                reportRequest.setBehaviorType("UNCOLLECT");
                reportRequest.setDurationSeconds(0);
                behaviorService.report(reportRequest);
            } else {
                // 重新收藏：状态改为0，记录正向行为
                favorite.setStatus(0);
                favorite.setFolderName(folderName);
                favorite.setUpdatedAt(LocalDateTime.now());
                userFavoriteRepository.save(favorite);

                recipe.setCollectCount(recipe.getCollectCount() + 1);
                recipeRepository.save(recipe);

                // 上报收藏行为（正向）
                BehaviorReportRequest reportRequest = new BehaviorReportRequest();
                reportRequest.setUserId(request.getUserId());
                reportRequest.setRecipeId(request.getRecipeId());
                reportRequest.setBehaviorType("COLLECT");
                reportRequest.setDurationSeconds(0);
                behaviorService.report(reportRequest);
            }
        } else {
            // 首次收藏
            UserFavorite favorite = new UserFavorite();
            favorite.setUserId(request.getUserId());
            favorite.setRecipeId(request.getRecipeId());
            favorite.setStatus(0);
            favorite.setFolderName(folderName);
            userFavoriteRepository.save(favorite);

            recipe.setCollectCount(recipe.getCollectCount() + 1);
            recipeRepository.save(recipe);

            // 上报收藏行为（正向）
            BehaviorReportRequest reportRequest = new BehaviorReportRequest();
            reportRequest.setUserId(request.getUserId());
            reportRequest.setRecipeId(request.getRecipeId());
            reportRequest.setBehaviorType("COLLECT");
            reportRequest.setDurationSeconds(0);
            behaviorService.report(reportRequest);
        }
    }

    @Override
    public Boolean checkFavorite(Long userId, Long recipeId) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");

        return userFavoriteRepository.existsByUserIdAndRecipeIdAndStatus(userId, recipeId, 0);
    }

    @Override
    public PageResult<RecipeResponse> listFavorites(Long userId, Integer page, Integer size) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<UserFavorite> favoritePage = userFavoriteRepository
                .findByUserIdAndStatus(userId, 0, pageable);

        List<Long> recipeIds = new ArrayList<>();
        for (UserFavorite fav : favoritePage.getContent()) {
            recipeIds.add(fav.getRecipeId());
        }

        if (recipeIds.isEmpty()) {
            return PageResult.success(new ArrayList<>(), 0, page, size);
        }

        List<Recipe> recipes = recipeRepository.findAllById(recipeIds);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (UserFavorite fav : favoritePage.getContent()) {
            for (Recipe recipe : recipes) {
                if (recipe.getId().equals(fav.getRecipeId())) {
                    dtoList.add(convertToResponse(recipe));
                    break;
                }
            }
        }

        return PageResult.success(dtoList, favoritePage.getTotalElements(), page, size);
    }

    // src/main/java/com/bdu/dishmind/service/impl/FavoriteServiceImpl.java

    @Override
    public List<String> getFolders(Long userId) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        List<String> folders = userFavoriteRepository.findDistinctFolderNamesByUserId(userId);

        // 确保至少返回"默认收藏夹"
        if (folders.isEmpty()) {
            folders.add("默认收藏夹");
        }

        return folders;
    }

    @Override
    @Transactional
    public void renameFolder(FavoriteFolderRenameRequest request) {
        ServiceUtil.requireNonNull(request.getUserId(), "用户ID不能为空");
        ServiceUtil.requireNonNull(request.getOldFolderName(), "原文件夹名不能为空");
        ServiceUtil.requireNonNull(request.getNewFolderName(), "新文件夹名不能为空");

        if (request.getOldFolderName().equals(request.getNewFolderName())) {
            return; // 同名无需修改
        }

        // 检查新文件夹名是否已存在（可选，根据业务决定是合并还是报错）
        // 这里直接重命名，如果有重复会合并
        userFavoriteRepository.updateFolderName(
                request.getUserId(),
                request.getOldFolderName(),
                request.getNewFolderName()
        );
    }

    @Override
    @Transactional
    public void deleteFolder(Long userId, String folderName) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        ServiceUtil.requireNonNull(folderName, "文件夹名不能为空");

        // 默认收藏夹不能删除
        if ("默认收藏夹".equals(folderName)) {
            throw new BizException("默认收藏夹不能删除");
        }

        // 将该文件夹下所有收藏移到默认收藏夹
        userFavoriteRepository.moveToDefaultFolder(userId, folderName);
    }

    @Override
    public PageResult<RecipeResponse> listFavoritesByFolder(Long userId, String folderName, Integer page, Integer size) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<UserFavorite> favoritePage;

        if (ServiceUtil.isEmpty(folderName)) {
            // 查全部
            favoritePage = userFavoriteRepository.findByUserIdAndStatus(userId, 0, pageable);
        } else {
            // 查指定文件夹
            favoritePage = userFavoriteRepository.findByUserIdAndFolderNameAndStatus(userId, folderName, 0, pageable);
        }

        // 组装RecipeResponse...
        List<Long> recipeIds = new ArrayList<>();
        for (UserFavorite fav : favoritePage.getContent()) {
            recipeIds.add(fav.getRecipeId());
        }

        if (recipeIds.isEmpty()) {
            return PageResult.success(new ArrayList<>(), 0, page, size);
        }

        List<Recipe> recipes = recipeRepository.findAllById(recipeIds);

        List<RecipeResponse> dtoList = new ArrayList<>();
        for (UserFavorite fav : favoritePage.getContent()) {
            for (Recipe recipe : recipes) {
                if (recipe.getId().equals(fav.getRecipeId())) {
                    dtoList.add(convertToResponse(recipe));
                    break;
                }
            }
        }

        return PageResult.success(dtoList, favoritePage.getTotalElements(), page, size);
    }

    @Override
    @Transactional
    public void moveFavoriteToFolder(Long userId, Long recipeId, String targetFolderName) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        if (ServiceUtil.isEmpty(targetFolderName)) {
            throw new BizException("目标文件夹名不能为空");
        }

        // 查询收藏记录（必须是正常状态）
        UserFavorite favorite = userFavoriteRepository
                .findByUserIdAndRecipeIdAndStatus(userId, recipeId, 0)
                .orElseThrow(() -> new BizException("收藏记录不存在或已删除"));

        // 更新文件夹名称
        favorite.setFolderName(targetFolderName);
        favorite.setUpdatedAt(LocalDateTime.now());
        userFavoriteRepository.save(favorite);
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

package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.*;
import com.bdu.dishmind.dto.response.RecipeDetailResponse;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.dto.response.RecipeStepResponse;
import com.bdu.dishmind.repository.TagRepository;
import com.bdu.dishmind.service.BehaviorService;
import com.bdu.dishmind.service.RecipeService;
import com.bdu.dishmind.service.RecipeStepService;
import com.bdu.dishmind.utils.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private BehaviorService behaviorService;
    @Autowired
    private RecipeStepService recipeStepService;
    @Autowired
    private TagRepository tagRepository;
    @GetMapping("/list")
    public ApiResult<PageResult<RecipeResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "hot") String sort,
            @RequestParam(required = false) Integer status) {  // 新增参数

        // 如果传了status，按status查；没传默认查正常的(2)
        if (status != null) {
            return ApiResult.success(recipeService.listByStatus(status, page, size, sort));
        }
        return ApiResult.success(recipeService.list(page, size, sort));
    }

    @GetMapping("/my")
    public ApiResult<PageResult<RecipeResponse>> listMy(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResult.success(recipeService.listMy(userId, page, size));
    }

    @GetMapping("/search")
    public ApiResult<PageResult<RecipeResponse>> search(RecipeSearchRequest request) {
        return ApiResult.success(recipeService.search(request));
    }

    @GetMapping("/filter")
    public ApiResult<PageResult<RecipeResponse>> filterByTag(
            @RequestParam List<Integer> tagIds,  // 改为List，支持多选
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // 参数校验
        if (tagIds == null || tagIds.isEmpty()) {
            throw new BizException("至少选择一个标签");
        }

        return ApiResult.success(recipeService.filterByTags(tagIds, page, size));
    }
    @GetMapping("/{id}")
    public ApiResult<RecipeDetailResponse> detail(@PathVariable Long id, @RequestParam Long userId) {
        return ApiResult.success(recipeService.getDetail(id, userId));
    }

    @GetMapping("/{id}/similar")
    public ApiResult<List<RecipeResponse>> similar(@PathVariable Long id, @RequestParam(defaultValue = "5") Integer limit) {
        return ApiResult.success(recipeService.findSimilar(id, limit));
    }


    @PostMapping("/{id}/dislike")
    public ApiResult<Void> dislike(@PathVariable Long id, @RequestParam Long userId) {
        BehaviorReportRequest request = new BehaviorReportRequest();
        request.setUserId(userId);
        request.setRecipeId(id);
        request.setBehaviorType("DISLIKE");
        request.setDurationSeconds(0);
        behaviorService.report(request);
        return ApiResult.success(null);
    }

    // 获取菜谱步骤列表（无需权限，任何人可看）
    @GetMapping("/{id}/steps")
    public ApiResult<List<RecipeStepResponse>> getSteps(@PathVariable Long id) {
        return ApiResult.success(recipeStepService.getStepsByRecipeId(id));
    }

    //批量保存步骤（路径包含recipeId）
    @PostMapping("/{recipeId}/steps")
    public ApiResult<Void> saveSteps(@PathVariable Long recipeId,
                                     @RequestBody List<RecipeStepRequest> steps,
                                     @RequestParam Long userId) {
        recipeStepService.saveSteps(recipeId, steps, userId);
        return ApiResult.success(null);
    }

    //更新单步骤（路径包含recipeId和stepId）
    @PostMapping("/{recipeId}/steps/{stepId}/update")
    public ApiResult<Void> updateStep(@PathVariable Long recipeId,
                                      @PathVariable Long stepId,
                                      @RequestBody RecipeStepRequest request,
                                      @RequestParam Long userId) {
        recipeStepService.updateStep(recipeId, stepId, request, userId);
        return ApiResult.success(null);
    }

    //删除单步骤（路径包含recipeId和stepId，软删除）
    @PostMapping("/{recipeId}/steps/{stepId}/delete")
    public ApiResult<Void> deleteStep(@PathVariable Long recipeId,
                                      @PathVariable Long stepId,
                                      @RequestParam Long userId) {
        recipeStepService.deleteStep(recipeId, stepId, userId);
        return ApiResult.success(null);
    }

    //恢复软删除步骤（路径包含recipeId和stepId）
    @PostMapping("/{recipeId}/steps/{stepId}/restore")
    public ApiResult<Void> restoreStep(@PathVariable Long recipeId,
                                       @PathVariable Long stepId,
                                       @RequestParam Long userId) {
        recipeStepService.restoreStep(recipeId, stepId, userId);
        return ApiResult.success(null);
    }
    @PostMapping("/create")
    public ApiResult<RecipeResponse> createRecipe(@RequestBody RecipeCreateRequest request,
                                                  @RequestParam Long userId) {
        return ApiResult.success(recipeService.createRecipe(request, userId));
    }

    @PostMapping("/{id}/republish")
    public ApiResult<Void> republish(@PathVariable Long id, @RequestParam Long userId) {
        recipeService.republish(id, userId);
        return ApiResult.success(null);
    }


    @PostMapping("/{id}/offline")
    public ApiResult<Void> offline(@PathVariable Long id, @RequestParam Long userId) {
        recipeService.offline(id, userId);
        return ApiResult.success(null);
    }

    // 审核通过（审核中→正常）
    @PostMapping("/{id}/approve")
    public ApiResult<Void> approveRecipe(@PathVariable Long id, @RequestParam Long adminId) {
        recipeService.approveRecipe(id, adminId);
        return ApiResult.success(null);
    }

    // 审核驳回（审核中→草稿）
    @PostMapping("/{id}/reject")
    public ApiResult<Void> rejectRecipe(@PathVariable Long id,
                                        @RequestParam Long adminId,
                                        @RequestParam(required = false) String reason) {
        recipeService.rejectRecipe(id, adminId, reason);
        return ApiResult.success(null);
    }
}

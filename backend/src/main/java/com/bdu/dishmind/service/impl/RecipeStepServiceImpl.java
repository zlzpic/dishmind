package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.request.RecipeCreateRequest;
import com.bdu.dishmind.dto.request.RecipeStepRequest;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.dto.response.RecipeStepResponse;
import com.bdu.dishmind.entity.*;
import com.bdu.dishmind.repository.*;
import com.bdu.dishmind.service.RecipeStepService;
import com.bdu.dishmind.utils.BizException;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecipeStepServiceImpl implements RecipeStepService {

    @Autowired
    private RecipeStepRepository recipeStepRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository; // 新增注入，用于查用户类型

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<RecipeStepResponse> getStepsByRecipeId(Long recipeId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");

        List<RecipeStep> steps = recipeStepRepository.findByRecipeIdAndStatusOrderByStepNumberAsc(recipeId, 0);
        List<RecipeStepResponse> result = new ArrayList<>();

        int displayOrder = 1; // 动态序号从1开始
        for (RecipeStep step : steps) {
            RecipeStepResponse dto = new RecipeStepResponse();
            dto.setId(step.getId());                    // 数据库主键
            dto.setStepNumber(step.getStepNumber());    // 原始序号（调试用，前端可忽略）
            dto.setDisplayOrder(displayOrder++);        //动态连续序号（1,2,3,4...）
            dto.setDescription(step.getDescription());
            dto.setImageUrl(step.getImageUrl());
            dto.setTip(step.getTip());
            dto.setDurationSeconds(step.getDurationSeconds());
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public void saveSteps(Long recipeId, List<RecipeStepRequest> steps, Long userId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        // 权限校验：只有作者或管理员能修改
        checkPermission(recipeId, userId);

        // 验证步骤号不重复
        Set<Integer> stepNumbers = new HashSet<>();
        for (RecipeStepRequest step : steps) {
            if (!stepNumbers.add(step.getStepNumber())) {
                throw new BizException("步骤号重复：" + step.getStepNumber());
            }
        }

        // 删除旧步骤
        recipeStepRepository.deleteByRecipeId(recipeId);

        // 插入新步骤
        for (RecipeStepRequest req : steps) {
            RecipeStep step = new RecipeStep();
            step.setRecipeId(recipeId);
            step.setStepNumber(req.getStepNumber()); // 使用请求中的序号
            step.setDescription(req.getDescription());
            step.setImageUrl(req.getImageUrl());
            step.setTip(req.getTip());
            step.setDurationSeconds(req.getDurationSeconds());
            step.setStatus(0);  // 显式设置状态为正常，避免为null
            recipeStepRepository.save(step);
        }

    }

    @Override
    @Transactional
    public void updateStep(Long recipeId, Long stepId, RecipeStepRequest request, Long userId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(stepId, "步骤ID不能为空");
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        RecipeStep step = recipeStepRepository.findById(stepId)
                .orElseThrow(() -> new BizException("步骤不存在"));

        // 新增：显式校验步骤是否属于该菜品
        if (!step.getRecipeId().equals(recipeId)) {
            throw new BizException("该步骤不属于此菜谱");
        }

        // 检查是否已软删除
        if (step.getStatus() != null && step.getStatus() == 1) {
            throw new BizException("该步骤已删除，无法修改");
        }

        // 校验权限（使用传入的recipeId）
        checkPermission(recipeId, userId);

        step.setDescription(request.getDescription());
        step.setImageUrl(request.getImageUrl());
        step.setTip(request.getTip());
        step.setDurationSeconds(request.getDurationSeconds());

        recipeStepRepository.save(step);
    }

    @Override
    @Transactional
    public void deleteStep(Long recipeId, Long stepId, Long userId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(stepId, "步骤ID不能为空");
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        RecipeStep step = recipeStepRepository.findById(stepId)
                .orElseThrow(() -> new BizException("步骤不存在"));

        // 新增：显式校验步骤是否属于该菜品
        if (!step.getRecipeId().equals(recipeId)) {
            throw new BizException("该步骤不属于此菜谱");
        }

        // 校验权限（使用传入的recipeId）
        checkPermission(recipeId, userId);

        // 软删除，改status=1
        step.setStatus(1);
        recipeStepRepository.save(step);
    }

    @Override
    @Transactional
    public void restoreStep(Long recipeId, Long stepId, Long userId) {
        ServiceUtil.requireNonNull(recipeId, "菜谱ID不能为空");
        ServiceUtil.requireNonNull(stepId, "步骤ID不能为空");
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        RecipeStep step = recipeStepRepository.findById(stepId)
                .orElseThrow(() -> new BizException("步骤不存在"));

        // 新增：显式校验步骤是否属于该菜品
        if (!step.getRecipeId().equals(recipeId)) {
            throw new BizException("该步骤不属于此菜谱");
        }

        // 校验权限（使用传入的recipeId）
        checkPermission(recipeId, userId);

        // 检查当前状态
        if (step.getStatus() == null || step.getStatus() == 0) {
            throw new BizException("该步骤未删除，无需恢复");
        }

        // 恢复：改status=0
        step.setStatus(0);
        recipeStepRepository.save(step);
    }

    /**
     * 权限校验：只有菜谱作者或管理员能操作
     */
    private void checkPermission(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new BizException("菜谱不存在"));

        // 是作者，直接通过
        if (recipe.getAuthorId().equals(userId)) {
            return;
        }

        // 不是作者，检查是否是管理员
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        if (user.getUserType() != null && user.getUserType() == 1) {
            return; // 管理员通过
        }

        // 既不是作者也不是管理员
        throw new BizException("无权限操作该菜谱");
    }
}

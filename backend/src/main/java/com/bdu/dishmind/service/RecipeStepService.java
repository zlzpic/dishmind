package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.request.RecipeCreateRequest;
import com.bdu.dishmind.dto.request.RecipeStepRequest;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.dto.response.RecipeStepResponse;

import java.util.List;

public interface RecipeStepService {
    List<RecipeStepResponse> getStepsByRecipeId(Long recipeId);
    //增加userId参数用于权限校验
    void saveSteps(Long recipeId, List<RecipeStepRequest> steps, Long userId);

    void updateStep(Long recipeId, Long stepId, RecipeStepRequest request, Long userId);

    void deleteStep(Long recipeId, Long stepId, Long userId);

    void restoreStep(Long recipeId, Long stepId, Long userId);
}

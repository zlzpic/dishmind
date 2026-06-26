package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.FavoriteRequest;
import com.bdu.dishmind.dto.request.RecipeCreateRequest;
import com.bdu.dishmind.dto.request.RecipeFilterRequest;
import com.bdu.dishmind.dto.request.RecipeSearchRequest;
import com.bdu.dishmind.dto.response.RecipeDetailResponse;
import com.bdu.dishmind.dto.response.RecipeResponse;

import java.util.List;

public interface RecipeService {
    PageResult<RecipeResponse> list(Integer page, Integer size, String sort);
    PageResult<RecipeResponse> listMy(Long userId, Integer page, Integer size);
    PageResult<RecipeResponse> search(RecipeSearchRequest request);
    PageResult<RecipeResponse> filterByTag(RecipeFilterRequest request);
    PageResult<RecipeResponse> filterByTags(List<Integer> tagIds, Integer page, Integer size);
    RecipeDetailResponse getDetail(Long id, Long userId);
    List<RecipeResponse> findSimilar(Long recipeId, Integer limit);
    RecipeResponse createRecipe(RecipeCreateRequest request, Long userId);
    void republish(Long recipeId, Long userId);
    void offline(Long recipeId, Long userId);
    void approveRecipe(Long recipeId, Long adminId);
    void rejectRecipe(Long recipeId, Long adminId, String reason);
    PageResult<RecipeResponse> listByStatus(Integer status, Integer page, Integer size, String sort);
}

package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.response.RecipeResponse;

import java.util.List;

public interface RecommendService {
    List<RecipeResponse> forYou(Long userId, Integer limit);
    PageResult<RecipeResponse> hot(Integer page, Integer size);
    PageResult<RecipeResponse> latest(Integer page, Integer size);
}

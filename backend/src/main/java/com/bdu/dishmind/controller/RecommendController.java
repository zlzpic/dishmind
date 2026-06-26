package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/for-you")
    public ApiResult<List<RecipeResponse>> forYou(@RequestParam Long userId, @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResult.success(recommendService.forYou(userId, limit));
    }

    @GetMapping("/hot")
    public ApiResult<PageResult<RecipeResponse>> hot(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResult.success(recommendService.hot(page, size));
    }

    @GetMapping("/latest")
    public ApiResult<PageResult<RecipeResponse>> latest(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResult.success(recommendService.latest(page, size));
    }
}

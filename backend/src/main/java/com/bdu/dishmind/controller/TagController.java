package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.response.TagCategoryResponse;
import com.bdu.dishmind.dto.response.TagResponse;
import com.bdu.dishmind.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/all")
    public ApiResult<List<TagCategoryResponse>> getAll() {
        return ApiResult.success(tagService.getAllWithCategories());
    }

    @GetMapping("/category/{categoryId}")
    public ApiResult<List<TagResponse>> getByCategory(@PathVariable Integer categoryId) {
        return ApiResult.success(tagService.getByCategory(categoryId));
    }
    @PostMapping("/dislike")
    public ApiResult<Void> dislikeTag(@RequestParam Long userId, @RequestParam Integer tagId) {
        tagService.dislikeTag(userId, tagId);
        return ApiResult.success(null);
    }
}

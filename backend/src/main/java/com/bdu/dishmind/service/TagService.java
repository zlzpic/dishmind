package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.response.TagCategoryResponse;
import com.bdu.dishmind.dto.response.TagResponse;

import java.util.List;

public interface TagService {
    List<TagCategoryResponse> getAllWithCategories();
    List<TagResponse> getByCategory(Integer categoryId);
    void dislikeTag(Long userId, Integer tagId);
}

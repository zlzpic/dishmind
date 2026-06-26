package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.request.TagCreateRequest;
import com.bdu.dishmind.dto.request.TagUpdateRequest;
import com.bdu.dishmind.dto.response.TagResponse;
import com.bdu.dishmind.entity.TagCategory;

import java.util.List;

public interface TagAdminService {
    TagResponse createTag(TagCreateRequest request, Long adminId);
    TagResponse updateTag(Integer tagId, TagUpdateRequest request, Long adminId);
    void deleteTag(Integer tagId, Long adminId);
    List<TagCategory> listCategories();
    TagCategory createCategory(String name, Long adminId);
}

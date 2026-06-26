package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.request.TagCreateRequest;
import com.bdu.dishmind.dto.request.TagUpdateRequest;
import com.bdu.dishmind.dto.response.TagResponse;
import com.bdu.dishmind.entity.TagCategory;
import com.bdu.dishmind.service.TagAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tag")
public class TagAdminController {

    @Autowired
    private TagAdminService tagAdminService;

    @GetMapping("/categories")
    public ApiResult<List<TagCategory>> listCategories() {
        return ApiResult.success(tagAdminService.listCategories());
    }

    @PostMapping("/category/create")
    public ApiResult<TagCategory> createCategory(@RequestParam String name, @RequestParam Long adminId) {
        return ApiResult.success(tagAdminService.createCategory(name, adminId));
    }

    @PostMapping("/create")
    public ApiResult<TagResponse> createTag(@RequestBody TagCreateRequest request, @RequestParam Long adminId) {
        return ApiResult.success(tagAdminService.createTag(request, adminId));
    }

    @PostMapping("/{id}/update")
    public ApiResult<TagResponse> updateTag(@PathVariable Integer id,
                                            @RequestBody TagUpdateRequest request,
                                            @RequestParam Long adminId) {
        return ApiResult.success(tagAdminService.updateTag(id, request, adminId));
    }

    @PostMapping("/{id}/delete")
    public ApiResult<Void> deleteTag(@PathVariable Integer id, @RequestParam Long adminId) {
        tagAdminService.deleteTag(id, adminId);
        return ApiResult.success(null);
    }
}

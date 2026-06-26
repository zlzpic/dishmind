package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.request.TagCreateRequest;
import com.bdu.dishmind.dto.request.TagUpdateRequest;
import com.bdu.dishmind.dto.response.TagResponse;
import com.bdu.dishmind.entity.Tag;
import com.bdu.dishmind.entity.TagCategory;
import com.bdu.dishmind.entity.User;
import com.bdu.dishmind.repository.*;
import com.bdu.dishmind.service.TagAdminService;
import com.bdu.dishmind.utils.BizException;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagAdminServiceImpl implements TagAdminService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagCategoryRepository tagCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeTagRepository recipeTagRepository;

    private void checkAdmin(Long adminId) {
        ServiceUtil.requireNonNull(adminId, "管理员ID不能为空");
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BizException("用户不存在"));
        if (admin.getUserType() == null || admin.getUserType() != 1) {
            throw new BizException("无权限，仅管理员可操作");
        }
    }

    @Override
    @Transactional
    public TagResponse createTag(TagCreateRequest request, Long adminId) {
        checkAdmin(adminId);

        ServiceUtil.requireNonNull(request.getName(), "标签名不能为空");
        ServiceUtil.requireNonNull(request.getCategoryId(), "分类ID不能为空");

        // 校验分类存在
        tagCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BizException("分类不存在"));

        // 校验标签名唯一（同分类下）
        if (tagRepository.existsByNameAndCategoryId(request.getName(), request.getCategoryId())) {
            throw new BizException("该分类下已存在同名标签");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setCategoryId(request.getCategoryId());
        tag.setUsageCount(0);

        Tag saved = tagRepository.save(tag);

        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public TagResponse updateTag(Integer tagId, TagUpdateRequest request, Long adminId) {
        checkAdmin(adminId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new BizException("标签不存在"));

        // 如果修改名称，校验唯一性
        if (request.getName() != null && !request.getName().equals(tag.getName())) {
            Integer categoryId = request.getCategoryId() != null ? request.getCategoryId() : tag.getCategoryId();
            if (tagRepository.existsByNameAndCategoryId(request.getName(), categoryId)) {
                throw new BizException("该分类下已存在同名标签");
            }
            tag.setName(request.getName());
        }

        if (request.getCategoryId() != null) {
            tagCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BizException("分类不存在"));
            tag.setCategoryId(request.getCategoryId());
        }

        Tag saved = tagRepository.save(tag);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTag(Integer tagId, Long adminId) {
        checkAdmin(adminId);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new BizException("标签不存在"));

        // 检查是否有关联菜谱
        long count = recipeTagRepository.countByTagId(tagId);
        if (count > 0) {
            throw new BizException("该标签已关联" + count + "个菜谱，不能删除");
        }

        tagRepository.delete(tag);
    }

    @Override
    public List<TagCategory> listCategories() {
        return tagCategoryRepository.findAll();
    }

    @Override
    @Transactional
    public TagCategory createCategory(String name, Long adminId) {
        checkAdmin(adminId);
        ServiceUtil.requireNonNull(name, "分类名不能为空");

        if (tagCategoryRepository.existsByName(name)) {
            throw new BizException("分类名已存在");
        }

        TagCategory category = new TagCategory();
        category.setName(name);
        return tagCategoryRepository.save(category);
    }

    private TagResponse convertToResponse(Tag tag) {
        TagResponse dto = new TagResponse();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setCategoryId(tag.getCategoryId());

        TagCategory category = tagCategoryRepository.findById(tag.getCategoryId()).orElse(null);
        dto.setCategoryName(category != null ? category.getName() : "");

        dto.setWeight(tag.getUsageCount() != null ? tag.getUsageCount().doubleValue() : 0.0);
        return dto;
    }
}

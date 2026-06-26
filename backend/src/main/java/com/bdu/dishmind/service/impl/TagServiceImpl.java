package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.response.TagCategoryResponse;
import com.bdu.dishmind.dto.response.TagResponse;
import com.bdu.dishmind.entity.Tag;
import com.bdu.dishmind.entity.TagCategory;
import com.bdu.dishmind.entity.UserTagPreference;
import com.bdu.dishmind.repository.TagCategoryRepository;
import com.bdu.dishmind.repository.TagRepository;
import com.bdu.dishmind.repository.UserTagPreferenceRepository;
import com.bdu.dishmind.service.TagService;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagCategoryRepository tagCategoryRepository;

    @Autowired
    private UserTagPreferenceRepository preferenceRepository;


    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<TagCategoryResponse> getAllWithCategories() {
        List<TagCategory> categories = tagCategoryRepository.findAll();
        List<TagCategoryResponse> result = new ArrayList<>();

        for (TagCategory category : categories) {
            TagCategoryResponse dto = new TagCategoryResponse();
            dto.setId(category.getId());
            dto.setName(category.getName());

            List<Tag> tags = tagRepository.findByCategoryId(category.getId());
            List<TagResponse> tagResponses = new ArrayList<>();
            for (Tag tag : tags) {
                TagResponse tr = new TagResponse();
                tr.setId(tag.getId());
                tr.setName(tag.getName());
                tr.setCategoryId(tag.getCategoryId());
                tr.setCategoryName(category.getName()); // 补全分类名
                // weight使用usage_count转换，或设为null（全局标签无特定权重）
                tr.setWeight(tag.getUsageCount() != null ? tag.getUsageCount().doubleValue() : 0.0);
                tagResponses.add(tr);
            }
            dto.setTags(tagResponses);
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<TagResponse> getByCategory(Integer categoryId) {
        ServiceUtil.requireNonNull(categoryId, "分类ID不能为空");

        // 先查分类名
        TagCategory category = tagCategoryRepository.findById(categoryId).orElse(null);
        String categoryName = category != null ? category.getName() : "";

        List<Tag> tags = tagRepository.findByCategoryId(categoryId);
        List<TagResponse> result = new ArrayList<>();

        for (Tag tag : tags) {
            TagResponse dto = new TagResponse();
            dto.setId(tag.getId());
            dto.setName(tag.getName());
            dto.setCategoryId(tag.getCategoryId());
            dto.setCategoryName(categoryName); // 补全分类名
            dto.setWeight(tag.getUsageCount() != null ? tag.getUsageCount().doubleValue() : 0.0);
            result.add(dto);
        }

        return result;
    }

    @Transactional
    public void dislikeTag(Long userId, Integer tagId) {
        Optional<UserTagPreference> exist = preferenceRepository.findByUserIdAndTagId(userId, tagId);

        if (exist.isPresent()) {
            UserTagPreference pref = exist.get();
            pref.setScore(pref.getScore().subtract(new BigDecimal("2.0")));
            pref.setNegativeCount(pref.getNegativeCount() + 1);
            pref.setLastInteraction(LocalDateTime.now());
            preferenceRepository.save(pref);
        } else {
            // 新建负分记录（首次 dislike）
            UserTagPreference pref = new UserTagPreference();
            pref.setUserId(userId);
            pref.setTagId(tagId);
            pref.setScore(new BigDecimal("-2.0"));
            pref.setPositiveCount(0);
            pref.setNegativeCount(1);
            pref.setLastInteraction(LocalDateTime.now());
            preferenceRepository.save(pref);
        }
    }
}

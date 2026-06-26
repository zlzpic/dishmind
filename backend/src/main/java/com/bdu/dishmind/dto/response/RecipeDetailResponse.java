package com.bdu.dishmind.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecipeDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private Long authorId;
    private String authorNickname;
    private String authorAvatarUrl;

    private Integer difficulty;
    private Integer cookTime;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer viewCount;
    private Integer collectCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TagResponse> tags;
    private Boolean isFavorited;

    // 新增：制作步骤列表
    private List<RecipeStepResponse> steps;

    // 新增：总步骤数
    private Integer totalSteps;

    // 新增：预计总耗时（秒）
    private Integer totalDuration;
}

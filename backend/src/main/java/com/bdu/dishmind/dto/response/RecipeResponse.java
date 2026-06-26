package com.bdu.dishmind.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecipeResponse {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private Long authorId;
    private String authorNickname;
    private Integer difficulty;
    private Integer cookTime;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer viewCount;
    private Integer collectCount;
    private Integer status;
    private LocalDateTime createdAt;
}

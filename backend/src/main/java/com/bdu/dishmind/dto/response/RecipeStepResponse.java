package com.bdu.dishmind.dto.response;

import lombok.Data;

@Data
public class RecipeStepResponse {
    private Long id;
    private Integer stepNumber;
    private Integer displayOrder;
    private String description;
    private String imageUrl;
    private String tip;
    private Integer durationSeconds;
}

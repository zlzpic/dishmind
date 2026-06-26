package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class RecipeStepRequest {
    private Integer stepNumber;
    private String description;
    private String imageUrl;
    private String tip;
    private Integer durationSeconds;
}

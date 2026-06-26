package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class BehaviorReportRequest {
    private Long userId;
    private Long recipeId;
    private String behaviorType;
    private Integer durationSeconds;
}

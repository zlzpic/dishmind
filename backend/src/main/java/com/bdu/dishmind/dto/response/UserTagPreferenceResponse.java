package com.bdu.dishmind.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserTagPreferenceResponse {
    private Integer tagId;
    private String tagName;
    private String categoryName;
    private BigDecimal score;
    private Integer positiveCount;
}

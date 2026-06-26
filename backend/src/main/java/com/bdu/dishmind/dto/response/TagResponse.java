package com.bdu.dishmind.dto.response;

import lombok.Data;

@Data
public class TagResponse {
    private Integer id;
    private String name;
    private Integer categoryId;
    private String categoryName;
    private Double weight;
}

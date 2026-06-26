package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class RecipeSearchRequest {
    private String keyword;
    private Integer page;
    private Integer size;
    private String sort;
}

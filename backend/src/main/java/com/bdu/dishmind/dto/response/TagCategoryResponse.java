package com.bdu.dishmind.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TagCategoryResponse {
    private Integer id;
    private String name;
    private List<TagResponse> tags;
}

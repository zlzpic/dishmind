package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class TagUpdateRequest {
    private String name;
    private Integer categoryId;
}

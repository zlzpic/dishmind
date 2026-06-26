package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class TagCreateRequest {
    private String name;
    private Integer categoryId;
}

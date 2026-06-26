package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class RecipeFilterRequest {
    private Integer tagId;
    private Integer page;
    private Integer size;
}

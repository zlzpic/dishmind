package com.bdu.dishmind.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RecipeCreateRequest {
    private String title;
    private String description;
    private String coverImage;
    private Integer difficulty;
    private Integer cookTime;
    private List<Integer> tagIds;        // 绑定的标签ID列表
    private List<RecipeStepRequest> steps;  // 步骤列表
}

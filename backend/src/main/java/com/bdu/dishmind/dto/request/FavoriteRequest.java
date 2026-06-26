package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class FavoriteRequest {
    private Long userId;
    private Long recipeId;
    private String folderName; // 可选，不传默认为"默认收藏夹"
}

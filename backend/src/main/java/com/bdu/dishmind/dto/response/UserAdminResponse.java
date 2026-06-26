package com.bdu.dishmind.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserAdminResponse {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private Integer userType;
    private Integer status;
    private LocalDateTime createdAt;

    // 统计字段
    private Integer recipeCount;      // 发布菜谱数
    private Integer favoriteCount;    // 收藏数
    private Integer behaviorCount;    // 行为记录数
}

package com.bdu.dishmind.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String avatarUrl;
    private Integer userType;
    private LocalDateTime createdAt;
    private Integer status;
}

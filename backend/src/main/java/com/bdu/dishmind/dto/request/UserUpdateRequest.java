package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private Long userId;
    private String nickname;
}

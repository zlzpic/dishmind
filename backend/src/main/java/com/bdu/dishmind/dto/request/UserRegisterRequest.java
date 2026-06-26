package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String phone;
}

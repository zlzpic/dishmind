package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.request.UserLoginRequest;
import com.bdu.dishmind.dto.request.UserRegisterRequest;
import com.bdu.dishmind.dto.request.UserUpdateRequest;
import com.bdu.dishmind.dto.response.UserResponse;
import com.bdu.dishmind.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResult<UserResponse> register(@RequestBody UserRegisterRequest request) {
        return ApiResult.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResult<UserResponse> login(@RequestBody UserLoginRequest request) {
        return ApiResult.success(userService.login(request));
    }

    @GetMapping("/profile")
    public ApiResult<UserResponse> getProfile(@RequestParam Long userId) {
        return ApiResult.success(userService.getById(userId));
    }

    @PostMapping("/profile/update")
    public ApiResult<UserResponse> updateProfile(@RequestBody UserUpdateRequest request) {
        return ApiResult.success(userService.updateNickname(request));
    }
}

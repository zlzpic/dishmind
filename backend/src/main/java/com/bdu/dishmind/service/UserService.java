package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.UserLoginRequest;
import com.bdu.dishmind.dto.request.UserRegisterRequest;
import com.bdu.dishmind.dto.request.UserUpdateRequest;
import com.bdu.dishmind.dto.response.UserResponse;
import com.bdu.dishmind.dto.response.UserTagPreferenceResponse;

import java.util.List;

public interface UserService {
    UserResponse register(UserRegisterRequest request);
    UserResponse login(UserLoginRequest request);
    UserResponse getById(Long userId);
    UserResponse updateNickname(UserUpdateRequest request);
    List<UserTagPreferenceResponse> getUserTopTags(Long userId, int limit);
}

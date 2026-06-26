package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.response.UserAdminResponse;

public interface UserAdminService {
    PageResult<UserAdminResponse> listUsers(String keyword, Integer page, Integer size);
    UserAdminResponse getUserDetail(Long userId);
    void toggleUserStatus(Long targetUserId, Long adminId);
}

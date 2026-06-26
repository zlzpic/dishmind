package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.response.UserAdminResponse;
import com.bdu.dishmind.service.UserAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
public class UserAdminController {

    @Autowired
    private UserAdminService userAdminService;

    @GetMapping("/list")
    public ApiResult<PageResult<UserAdminResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResult.success(userAdminService.listUsers(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResult<UserAdminResponse> detail(@PathVariable Long id) {
        return ApiResult.success(userAdminService.getUserDetail(id));
    }

    @PostMapping("/{id}/toggleStatus")
    public ApiResult<Void> toggleStatus(@PathVariable Long id, @RequestParam Long adminId) {
        userAdminService.toggleUserStatus(id, adminId);
        return ApiResult.success(null);
    }
}

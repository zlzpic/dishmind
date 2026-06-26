package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.response.UserAdminResponse;
import com.bdu.dishmind.entity.User;
import com.bdu.dishmind.repository.*;
import com.bdu.dishmind.service.UserAdminService;
import com.bdu.dishmind.utils.BizException;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserAdminServiceImpl implements UserAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Override
    public PageResult<UserAdminResponse> listUsers(String keyword, Integer page, Integer size) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<User> userPage;

        if (ServiceUtil.isEmpty(keyword)) {
            userPage = userRepository.findAll(pageable);
        } else {
            // 模糊搜索用户名或昵称
            userPage = userRepository.findByUsernameContainingOrNicknameContaining(keyword, keyword, pageable);
        }

        List<UserAdminResponse> dtoList = new ArrayList<>();
        for (User user : userPage.getContent()) {
            dtoList.add(convertToAdminResponse(user));
        }

        return PageResult.success(dtoList, userPage.getTotalElements(), page, size);
    }

    @Override
    public UserAdminResponse getUserDetail(Long userId) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        return convertToAdminResponse(user);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long targetUserId, Long adminId) {
        ServiceUtil.requireNonNull(targetUserId, "目标用户ID不能为空");
        ServiceUtil.requireNonNull(adminId, "管理员ID不能为空");

        // 校验管理员
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new BizException("管理员不存在"));
        if (admin.getUserType() == null || admin.getUserType() != 1) {
            throw new BizException("无权限，仅管理员可操作");
        }

        // 不能禁用自己
        if (targetUserId.equals(adminId)) {
            throw new BizException("不能禁用自己");
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BizException("目标用户不存在"));

        // 不能禁用其他管理员
        if (target.getUserType() != null && target.getUserType() == 1) {
            throw new BizException("不能禁用管理员账号");
        }

        // 切换状态：1正常→0禁用，0禁用→1正常
        target.setStatus(target.getStatus() == null || target.getStatus() == 1 ? 0 : 1);
        userRepository.save(target);
    }

    private UserAdminResponse convertToAdminResponse(User user) {
        UserAdminResponse dto = new UserAdminResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setUserType(user.getUserType());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());

        // 查询统计（简单实现，可优化）
        dto.setRecipeCount(recipeRepository.countByAuthorId(user.getId()));
        dto.setFavoriteCount((int) userFavoriteRepository.countByUserIdAndStatus(user.getId(), 0));
        dto.setBehaviorCount((int) userBehaviorRepository.countByUserId(user.getId()));

        return dto;
    }
}

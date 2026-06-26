package com.bdu.dishmind.service.impl;

import com.bdu.dishmind.dto.request.UserLoginRequest;
import com.bdu.dishmind.dto.request.UserRegisterRequest;
import com.bdu.dishmind.dto.request.UserUpdateRequest;
import com.bdu.dishmind.dto.response.UserResponse;
import com.bdu.dishmind.dto.response.UserTagPreferenceResponse;
import com.bdu.dishmind.entity.User;
import com.bdu.dishmind.entity.UserTagPreference;
import com.bdu.dishmind.repository.UserRepository;
import com.bdu.dishmind.repository.UserTagPreferenceRepository;
import com.bdu.dishmind.service.UserService;
import com.bdu.dishmind.utils.BizException;
import com.bdu.dishmind.utils.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTagPreferenceRepository userTagPreferenceRepository;

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        ServiceUtil.requireNonNull(request.getUsername(), "用户名不能为空");
        ServiceUtil.requireNonNull(request.getPassword(), "密码不能为空");

        Optional<User> existUser = userRepository.findByUsername(request.getUsername());
        if (existUser.isPresent()) {
            throw new BizException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setUserType(0);
        user.setStatus(1);

        User saved = userRepository.save(user);
        return convertToResponse(saved);
    }

    @Override
    public UserResponse login(UserLoginRequest request) {
        ServiceUtil.requireNonNull(request.getUsername(), "用户名不能为空");
        ServiceUtil.requireNonNull(request.getPassword(), "密码不能为空");

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BizException("用户不存在"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BizException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }

        return convertToResponse(user);
    }

    @Override
    public UserResponse getById(Long userId) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateNickname(UserUpdateRequest request) {
        ServiceUtil.requireNonNull(request.getUserId(), "用户ID不能为空");
        ServiceUtil.requireNonNull(request.getNickname(), "昵称不能为空");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BizException("用户不存在"));

        user.setNickname(request.getNickname());
        User updated = userRepository.save(user);
        return convertToResponse(updated);
    }

    @Override
    public List<UserTagPreferenceResponse> getUserTopTags(Long userId, int limit) {
        ServiceUtil.requireNonNull(userId, "用户ID不能为空");

        List<UserTagPreference> prefs = userTagPreferenceRepository
                .findTopByUserIdOrderByScoreDesc(userId, PageRequest.of(0, limit));

        List<UserTagPreferenceResponse> result = new ArrayList<>();
        for (UserTagPreference pref : prefs) {
            UserTagPreferenceResponse dto = new UserTagPreferenceResponse();
            dto.setTagId(pref.getTagId());
            dto.setScore(pref.getScore());
            dto.setPositiveCount(pref.getPositiveCount());
            result.add(dto);
        }
        return result;
    }

    private UserResponse convertToResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setUserType(user.getUserType());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}

package com.rejs.reservation.domain.user.service;

import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.exception.UserBusinessExceptionCode;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // # CREATE
    @Transactional
    public UserDto createUser(String username, String password) {
        if(userRepository.existsByEmail(username)) {
            throw BusinessException.of(UserBusinessExceptionCode.USERNAME_ALREADY_EXISTS);
        }
        User user = new User(username, password);
        user = userRepository.save(user);
        return UserDto.of(user);
    }

    // # READ
    public UserDto findByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> BusinessException.of(UserBusinessExceptionCode.USER_NOT_FOUND));
        return UserDto.of(user);
    }

    public UserDto findById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> BusinessException.of(UserBusinessExceptionCode.USER_NOT_FOUND));
        return UserDto.of(user);
    }

}

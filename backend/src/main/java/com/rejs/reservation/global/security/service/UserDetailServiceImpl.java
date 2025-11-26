package com.rejs.reservation.global.security.service;

import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.security.exception.AuthenticationExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.rejs.reservation.domain.user.entity.User user = userRepository.findByUsername(username).orElseThrow(() -> BusinessException.of(AuthenticationExceptionCode.USER_INFO_MISMATCH));
        return new User(user.getId().toString(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
}

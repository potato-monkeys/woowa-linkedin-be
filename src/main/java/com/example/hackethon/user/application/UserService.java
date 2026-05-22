package com.example.hackethon.user.application;

import com.example.hackethon.user.domain.User;
import com.example.hackethon.user.domain.UserRepository;
import com.example.hackethon.user.dto.UserCreateRequest;
import com.example.hackethon.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long create(UserCreateRequest request) {
        User user = new User(request.name(), request.introduction());
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + id));
        return UserResponse.from(user);
    }
}

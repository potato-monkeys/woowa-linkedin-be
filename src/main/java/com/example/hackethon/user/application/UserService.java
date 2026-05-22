package com.example.hackethon.user.application;

import com.example.hackethon.common.exception.BusinessException;
import com.example.hackethon.security.JwtProvider;
import com.example.hackethon.storage.LocalFileStorageService;
import com.example.hackethon.user.domain.User;
import com.example.hackethon.user.domain.UserRepository;
import com.example.hackethon.user.dto.LoginRequest;
import com.example.hackethon.user.dto.LoginResponse;
import com.example.hackethon.user.dto.SignupRequest;
import com.example.hackethon.user.dto.UpdateProfileRequest;
import com.example.hackethon.user.dto.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final LocalFileStorageService fileStorageService;

    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException("이미 사용중인 닉네임입니다.", HttpStatus.CONFLICT);
        }
        User user = new User(request.nickname(), passwordEncoder.encode(request.password()), request.introduction());
        return userRepository.save(user).getId();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByNickname(request.nickname())
                .orElseThrow(() -> new BusinessException("닉네임 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("닉네임 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
        return new LoginResponse(jwtProvider.generateToken(user.getId()));
    }

    public UserResponse getMyProfile(Long userId) {
        return UserResponse.from(findUserById(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserById(userId);

        if (request.nickname() != null) {
            if (userRepository.existsByNickname(request.nickname())) {
                throw new BusinessException("이미 사용중인 닉네임입니다.", HttpStatus.CONFLICT);
            }
            user.updateNickname(request.nickname());
        }
        if (request.introduction() != null) {
            user.updateIntroduction(request.introduction());
        }
        if (request.newPassword() != null) {
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new BusinessException("현재 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
            }
            user.updatePassword(passwordEncoder.encode(request.newPassword()));
        }
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfileImage(Long userId, MultipartFile file) {
        User user = findUserById(userId);
        user.updateProfileImageUrl(fileStorageService.store(file, "profile-images"));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateRecording(Long userId, MultipartFile file, String defaultUrl) {
        User user = findUserById(userId);
        String url;
        if (file != null && !file.isEmpty()) {
            url = fileStorageService.store(file, "recordings");
        } else if (defaultUrl != null && !defaultUrl.isBlank()) {
            url = defaultUrl;
        } else {
            throw new BusinessException("파일 또는 기본 녹음을 선택해주세요.", HttpStatus.BAD_REQUEST);
        }
        user.updateRecordingFileUrl(url);
        return UserResponse.from(user);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(findUserById(id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}

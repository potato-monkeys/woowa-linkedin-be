package com.example.hackethon.user.ui;

import com.example.hackethon.security.UserPrincipal;
import com.example.hackethon.user.application.UserService;
import com.example.hackethon.user.dto.LoginRequest;
import com.example.hackethon.user.dto.LoginResponse;
import com.example.hackethon.user.dto.SignupRequest;
import com.example.hackethon.user.dto.UpdateProfileRequest;
import com.example.hackethon.user.dto.UserResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        Long id = userService.signup(request);
        return ResponseEntity.created(URI.create("/api/users/" + id)).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getMyProfile(principal.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(principal.getId(), request));
    }

    @PostMapping("/me/profile-image")
    public ResponseEntity<UserResponse> updateProfileImage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateProfileImage(principal.getId(), file));
    }

    @PostMapping("/me/recording")
    public ResponseEntity<UserResponse> updateRecording(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "defaultUrl", required = false) String defaultUrl) {
        return ResponseEntity.ok(userService.updateRecording(principal.getId(), file, defaultUrl));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}

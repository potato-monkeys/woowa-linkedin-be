package com.example.hackethon.user.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hackethon.user.dto.LoginRequest;
import com.example.hackethon.user.dto.SignupRequest;
import com.example.hackethon.user.dto.UpdateProfileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 후 로그인하면 토큰을 받는다")
    void signupAndLogin() throws Exception {
        // 회원가입
        SignupRequest signupRequest = new SignupRequest("testuser", "password123", "안녕하세요!");
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated());

        // 로그인
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        String accessToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
        assertThat(accessToken).isNotBlank();
    }

    @Test
    @DisplayName("JWT로 내 정보를 조회한다")
    void getMyProfile() throws Exception {
        String token = signupAndGetToken("alice", "pw123", "안녕!");

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("alice"))
                .andExpect(jsonPath("$.introduction").value("안녕!"));
    }

    @Test
    @DisplayName("JWT 없이 보호된 API를 호출하면 401을 반환한다")
    void unauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원정보를 수정한다")
    void updateProfile() throws Exception {
        String token = signupAndGetToken("bob", "pw123", "소개글");

        UpdateProfileRequest request = new UpdateProfileRequest(null, "수정된 소개글", null, null);
        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.introduction").value("수정된 소개글"));
    }

    @Test
    @DisplayName("중복된 닉네임으로 회원가입하면 409를 반환한다")
    void duplicateNickname() throws Exception {
        SignupRequest request = new SignupRequest("duplicate", "pw123", "소개");
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    private String signupAndGetToken(String nickname, String password, String introduction) throws Exception {
        SignupRequest signup = new SignupRequest(nickname, password, introduction);
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest(nickname, password);
        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }
}

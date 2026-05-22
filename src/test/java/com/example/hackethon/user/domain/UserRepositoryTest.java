package com.example.hackethon.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User를 저장하고 조회한다")
    void saveAndFind() {
        // given
        User user = new User("홍길동", "password123", "안녕하세요. 홍길동입니다.");

        // when
        User savedUser = userRepository.save(user);

        // then
        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(foundUser.getNickname()).isEqualTo("홍길동");
        assertThat(foundUser.getIntroduction()).isEqualTo("안녕하세요. 홍길동입니다.");
    }

    @Test
    @DisplayName("닉네임으로 User를 조회한다")
    void findByNickname() {
        // given
        userRepository.save(new User("testuser", "password123", "테스트 유저입니다."));

        // when & then
        assertThat(userRepository.findByNickname("testuser")).isPresent();
        assertThat(userRepository.existsByNickname("testuser")).isTrue();
        assertThat(userRepository.existsByNickname("없는닉네임")).isFalse();
    }
}

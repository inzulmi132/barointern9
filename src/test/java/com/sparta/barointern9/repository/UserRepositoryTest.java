package com.sparta.barointern9.repository;

import com.sparta.barointern9.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByUsername 테스트 1")
    public void test1() {
        // given
        User user = userRepository.save(
                User.builder()
                        .username("test")
                        .password("1234")
                        .nickname("test")
                        .build()
        );

        // when
        Optional<User> result = userRepository.findByUsername("test");

        // then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("findByUsername 테스트 2")
    public void test2() {
        // given
        userRepository.save(
                User.builder()
                        .username("test2")
                        .password("1234")
                        .nickname("test2")
                        .build()
        );

        // when
        Optional<User> result = userRepository.findByUsername("test");

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("existsByUsername 테스트 1")
    public void test3() {
        // given
        userRepository.save(
                User.builder()
                        .username("test")
                        .password("1234")
                        .nickname("test")
                        .build()
        );

        // when
        boolean result = userRepository.existsByUsername("test");

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("existsByUsername 테스트 2")
    public void test4() {
        // given
        userRepository.save(
                User.builder()
                        .username("test2")
                        .password("1234")
                        .nickname("test2")
                        .build()
        );

        // when
        boolean result = userRepository.existsByUsername("test");

        // then
        assertFalse(result);
    }
}

package com.sparta.barointern9.repository;

import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.entity.User;
import com.sparta.barointern9.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AuthorityRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    @DisplayName("findByUserUsername() - 1개 테스트")
    @Transactional
    public void test1() {
        // given
        User user = userRepository.save(
                User.builder()
                        .username("test")
                        .password("1234")
                        .nickname("test")
                        .build()
        );
        Authority authority = authorityRepository.save(
                Authority.builder()
                        .user(user)
                        .userRole(UserRole.ROLE_USER)
                        .build()
        );

        // when
        List<Authority> authorities = authorityRepository.findAllByUserUsername(user.getUsername());

        // then
        assertEquals(1, authorities.size());
        assertEquals(authority, authorities.get(0));
    }

    @Test
    @DisplayName("findByUserUsername() - 여러 개 테스트")
    @Transactional
    public void test2() {
        // given
        User user = userRepository.save(
                User.builder()
                        .username("test")
                        .password("1234")
                        .nickname("test")
                        .build()
        );
        Authority authority1 = authorityRepository.save(
                Authority.builder()
                        .user(user)
                        .userRole(UserRole.ROLE_USER)
                        .build()
        );
        Authority authority2 = authorityRepository.save(
                Authority.builder()
                        .user(user)
                        .userRole(UserRole.ROLE_ADMIN)
                        .build()
        );

        // when
        List<Authority> authorities = authorityRepository.findAllByUserUsername(user.getUsername());

        // then
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(authority1));
        assertTrue(authorities.contains(authority2));
    }

    @Test
    @DisplayName("deleteAllByUser() 테스트")
    @Transactional
    public void test3() {
        // given
        User user = userRepository.save(
                User.builder()
                        .username("test")
                        .password("1234")
                        .nickname("test")
                        .build()
        );
        authorityRepository.save(
                Authority.builder()
                        .user(user)
                        .userRole(UserRole.ROLE_USER)
                        .build()
        );
        authorityRepository.save(
                Authority.builder()
                        .user(user)
                        .userRole(UserRole.ROLE_ADMIN)
                        .build()
        );

        // when
        authorityRepository.deleteAllByUser(user);
        List<Authority> authorities = authorityRepository.findAllByUserUsername(user.getUsername());

        // then
        assertEquals(0, authorities.size());
    }
}

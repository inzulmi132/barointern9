package com.sparta.barointern9.service;

import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.enums.UserRole;
import com.sparta.barointern9.repository.AuthorityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {
    @Mock
    private AuthorityRepository authorityRepository;
    @InjectMocks
    private AuthorityService authorityService;

    @Test
    @DisplayName("getAuthorities() 테스트")
    public void test1() {
        // given
        Authority authority = Authority.builder()
                .userRole(UserRole.ROLE_USER)
                .build();
        when(authorityRepository.findAllByUserUsername(anyString())).thenReturn(List.of(authority));

        // when
        Collection<GrantedAuthority> authorities = authorityService.getAuthorities("test");

        // then
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
    }
}

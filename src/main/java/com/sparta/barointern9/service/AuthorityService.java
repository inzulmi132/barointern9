package com.sparta.barointern9.service;

import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.entity.User;
import com.sparta.barointern9.enums.UserRole;
import com.sparta.barointern9.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public Collection<GrantedAuthority> getAuthorities(String username) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorityRepository.findAllByUserUsername(username)
                .forEach(authority -> authorities.add(authority::toString));
        return authorities;
    }

    public Authority createAuthority(User user, UserRole userRole) {
        Authority authority = Authority.builder()
                .user(user)
                .userRole(userRole)
                .build();

        return authorityRepository.save(authority);
    }

    public void deleteAuthorityByUser(User user) {
        authorityRepository.deleteAllByUser(user);
    }
}

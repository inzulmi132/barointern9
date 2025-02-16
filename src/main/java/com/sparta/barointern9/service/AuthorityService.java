package com.sparta.barointern9.service;

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
                .forEach(authority -> authorities.add(authority.getUserRole()::toString));
        return authorities;
    }
}

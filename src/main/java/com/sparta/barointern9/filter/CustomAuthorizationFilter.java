package com.sparta.barointern9.filter;

import com.sparta.barointern9.jwt.JwtUtil;
import com.sparta.barointern9.service.AuthorityService;
import com.sparta.barointern9.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthorityService authorityService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = jwtUtil.getAccessTokenFromRequest(request);
        if(!StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        jwtUtil.validateToken(accessToken);

        String username = jwtUtil.getSubjectFromToken(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Collection<GrantedAuthority> authorities = authorityService.getAuthorities(username);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}

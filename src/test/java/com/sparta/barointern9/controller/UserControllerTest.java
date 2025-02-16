package com.sparta.barointern9.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sparta.barointern9.dto.request.SignRequestDto;
import com.sparta.barointern9.dto.request.SignupRequestDto;
import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.entity.User;
import com.sparta.barointern9.enums.UserRole;
import com.sparta.barointern9.exception.CustomApiException;
import com.sparta.barointern9.exception.ErrorCode;
import com.sparta.barointern9.jwt.JwtUtil;
import com.sparta.barointern9.repository.AuthorityRepository;
import com.sparta.barointern9.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void resetUserRepository() {
        authorityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("signup 성공 테스트")
    void test01() throws Exception {
        // given
        String username = "test";
        String password = "1234";
        String nickname = "test";
        SignupRequestDto requestDto = new SignupRequestDto(username, password, nickname);

        // when
        MvcResult result = mockMvc.perform(
                        post("/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isCreated())
                .andReturn();
        LinkedHashMap<String, Object> responseMap = JsonPath.parse(result.getResponse().getContentAsString()).read("$");

        // then
        assertNotNull(responseMap);
        assertEquals(username, responseMap.get("username"));
        assertEquals(nickname, responseMap.get("nickname"));
    }

    @Test
    @DisplayName("signup 실패 테스트")
    void test02() throws Exception {
        // given
        String username = "test";
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = "test";
        SignupRequestDto requestDto = new SignupRequestDto(username, password, nickname);
        userRepository.save(
                User.builder()
                        .username(username)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .build()
        );

        // when
        MvcResult result = mockMvc.perform(
                        post("/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isConflict())
                .andReturn();
        LinkedHashMap<String, Object> responseMap = JsonPath.parse(result.getResponse().getContentAsString()).read("$");

        // then
        assertNotNull(responseMap);
        assertEquals(ErrorCode.ALREADY_EXIST_USERNAME.getMessage(), responseMap.get("msg"));
    }

    @Test
    @DisplayName("signoff 성공 테스트")
    void test03() throws Exception {
        // given
        String username = "test";
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = "test";

        User user = userRepository.save(
                User.builder()
                        .username(username)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .build()
        );
        authorityRepository.save(Authority.builder().user(user).userRole(UserRole.ROLE_USER).build());

        Date expiration = new Date(System.currentTimeMillis() + JwtUtil.ACCESS_TOKEN_EXPIRATION);
        String accessToken = jwtUtil.generateToken(username, expiration);

        // when - then
        mockMvc.perform(
                        delete("/signoff")
                                .header(JwtUtil.ACCESS_TOKEN_HEADER, JwtUtil.BEARER_PREFIX + accessToken)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("signoff 실패 테스트")
    void test04() {
        // given
        String username = "test";
        Date expiration = new Date(System.currentTimeMillis() + JwtUtil.ACCESS_TOKEN_EXPIRATION);
        String accessToken = jwtUtil.generateToken(username, expiration);

        // when - then
        CustomApiException exception = assertThrows(CustomApiException.class,
                () -> mockMvc.perform(
                        delete("/signoff")
                                .header(JwtUtil.ACCESS_TOKEN_HEADER, JwtUtil.BEARER_PREFIX + accessToken)
                )
        );
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("sign 성공 테스트")
    void test05() throws Exception {
        // given
        String username = "test";
        String password = "1234";
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = "test";

        User user = userRepository.save(
                User.builder()
                        .username(username)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .build()
        );
        authorityRepository.save(
                Authority.builder()
                        .user(user)
                        .userRole(UserRole.ROLE_USER)
                        .build()
        );
        SignRequestDto requestDto = new SignRequestDto(username, password);

        // when
        MvcResult result = mockMvc.perform(
                        post("/sign")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                )
                .andExpect(status().isOk())
                .andReturn();
        LinkedHashMap<String, Object> responseMap = JsonPath.parse(result.getResponse().getContentAsString()).read("$");

        // then
        assertNotNull(responseMap);
        assertEquals(JwtUtil.BEARER_PREFIX, responseMap.get("grantType"));
        assertEquals(username, jwtUtil.getSubjectFromToken((String) responseMap.get("accessToken")));
        assertEquals(username, jwtUtil.getSubjectFromToken((String) responseMap.get("refreshToken")));
    }
}

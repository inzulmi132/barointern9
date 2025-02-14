package com.sparta.barointern9.controller;

import com.sparta.barointern9.dto.request.RefreshRequestDto;
import com.sparta.barointern9.dto.request.SignoutRequestDto;
import com.sparta.barointern9.dto.request.SignupRequestDto;
import com.sparta.barointern9.dto.response.SignupResponseDto;
import com.sparta.barointern9.dto.response.TokenResponseDto;
import com.sparta.barointern9.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.signup(requestDto));
    }

    @DeleteMapping("/signout")
    public ResponseEntity<Void> signout(@RequestBody SignoutRequestDto requestDto) {
        userService.signout(requestDto);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.refresh(requestDto));
    }
}

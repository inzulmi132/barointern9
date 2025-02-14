package com.sparta.barointern9.controller;

import com.sparta.barointern9.dto.request.RefreshRequestDto;
import com.sparta.barointern9.dto.request.SignRequestDto;
import com.sparta.barointern9.dto.request.SignupRequestDto;
import com.sparta.barointern9.dto.response.SignupResponseDto;
import com.sparta.barointern9.dto.response.TokenResponseDto;
import com.sparta.barointern9.exception.CustomApiException;
import com.sparta.barointern9.exception.CustomErrorResponseDto;
import com.sparta.barointern9.exception.ErrorCode;
import com.sparta.barointern9.service.UserService;
import com.sparta.barointern9.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "회원 계정 관리 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "유저가 회원가입 하는 API")
    @Parameter(name = "username", description = "계정 명")
    @Parameter(name = "password", description = "계정 비밀번호")
    @Parameter(name = "nickname", description = "계정 별명")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SignupResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 계정 명 입니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.signup(requestDto));
    }

    @DeleteMapping("/signoff")
    @Operation(summary = "회원탈퇴", description = "유저가 회원탈퇴 하는 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "회원탈퇴 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                        1. 유효하지 않는 JWT 서명 입니다.
                        2. 지원되지 않는 JWT 토큰 입니다.
                        3. 잘못된 JWT 토큰 입니다.
                        4. 만료된 JWT 토큰 입니다.
                        """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<Void> signoff(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.signoff(userDetails.getUser());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "토큰 검증 후 토큰 재발급")
    @Parameter(name = "refreshToken", description = "Refresh Token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                        1. 유효하지 않는 JWT 서명 입니다.
                        2. 지원되지 않는 JWT 토큰 입니다.
                        3. 잘못된 JWT 토큰 입니다.
                        4. 만료된 JWT 토큰 입니다.
                        """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RefreshRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.refresh(requestDto));
    }

    @PostMapping("/sign")
    @Operation(summary = "유저 로그인", description = "계정 명과 비밀번호로 로그인 후 토큰 발급")
    @Parameter(name = "username", description = "계정 명")
    @Parameter(name = "password", description = "계정 비밀번호")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "계정 명 또는 비밀번호가 잘못되었습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponseDto.class)
                    )
            )
    })
    public ResponseEntity<TokenResponseDto> sign(@RequestBody SignRequestDto requestDto) {
        throw new CustomApiException(ErrorCode.UNEXPECTED_API_REQUEST);
    }
}

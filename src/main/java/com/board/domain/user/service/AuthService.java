package com.board.domain.user.service;

import com.board.domain.user.dto.*;
import com.board.domain.user.entity.Role;
import com.board.domain.user.entity.User;
import com.board.domain.user.repository.UserRepository;
import com.board.global.exception.BusinessException;
import com.board.global.exception.ErrorCode;
import com.board.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException("이미 사용 중인 닉네임입니다.", ErrorCode.DUPLICATE_EMAIL);
        }

        // User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        // 비밀번호 암호화
        user.encodePassword(passwordEncoder);

        // 저장
        User savedUser = userRepository.save(user);
        log.info("새로운 사용자 가입: {}", savedUser.getEmail());

        return UserResponse.from(savedUser);
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!user.matchPassword(passwordEncoder, request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().getKey()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        Long expiresIn = jwtTokenProvider.getExpiration(accessToken);

        log.info("사용자 로그인: {}", user.getEmail());

        return TokenResponse.of(accessToken, refreshToken, expiresIn);
    }

    /**
     * 토큰 재발급
     */
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // 사용자 조회
        User user = userRepository.findActiveUserByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().getKey()
        );
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        Long expiresIn = jwtTokenProvider.getExpiration(newAccessToken);

        log.info("토큰 재발급: {}", user.getEmail());

        return TokenResponse.of(newAccessToken, newRefreshToken, expiresIn);
    }
}

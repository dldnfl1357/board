package com.board.service;

import com.board.domain.user.dto.LoginRequest;
import com.board.domain.user.dto.SignUpRequest;
import com.board.domain.user.dto.TokenResponse;
import com.board.domain.user.dto.UserResponse;
import com.board.domain.user.entity.Role;
import com.board.domain.user.entity.User;
import com.board.domain.user.repository.UserRepository;
import com.board.domain.user.service.AuthService;
import com.board.global.exception.BusinessException;
import com.board.global.exception.ErrorCode;
import com.board.global.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 실패 케이스 테스트")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("실패 1: 회원가입 시 이메일 중복")
    void signUp_Fail_DuplicateEmail() {
        // given
        SignUpRequest request = new SignUpRequest(
                "duplicate@example.com",
                "Test1234!",
                "테스터"
        );

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패 2: 회원가입 시 닉네임 중복")
    void signUp_Fail_DuplicateNickname() {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@example.com",
                "Test1234!",
                "중복닉네임"
        );

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByNickname(request.getNickname())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패 3: 로그인 시 존재하지 않는 사용자")
    void login_Fail_UserNotFound() {
        // given
        LoginRequest request = new LoginRequest(
                "nonexistent@example.com",
                "Test1234!"
        );

        given(userRepository.findActiveUserByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND)
                .hasMessage("User not found");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).createAccessToken(anyString(), anyString());
    }

    @Test
    @DisplayName("실패 4: 로그인 시 비밀번호 불일치")
    void login_Fail_InvalidPassword() {
        // given
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "WrongPassword123!"
        );

        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("테스터")
                .role(Role.USER)
                .build();

        given(userRepository.findActiveUserByEmail(request.getEmail()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD)
                .hasMessage("Invalid password");

        verify(jwtTokenProvider, never()).createAccessToken(anyString(), anyString());
    }

    @Test
    @DisplayName("실패 5: 로그인 시 삭제된 사용자")
    void login_Fail_DeletedUser() {
        // given
        LoginRequest request = new LoginRequest(
                "deleted@example.com",
                "Test1234!"
        );

        // deleted=true인 사용자는 findActiveUserByEmail에서 조회되지 않음
        given(userRepository.findActiveUserByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("실패 6: 토큰 재발급 시 유효하지 않은 Refresh Token")
    void refreshToken_Fail_InvalidToken() {
        // given
        String invalidRefreshToken = "invalid.refresh.token";

        given(jwtTokenProvider.validateToken(invalidRefreshToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED)
                .hasMessage("Unauthorized");

        verify(jwtTokenProvider, never()).getEmailFromToken(anyString());
        verify(userRepository, never()).findActiveUserByEmail(anyString());
    }

    @Test
    @DisplayName("실패 7: 토큰 재발급 시 토큰의 사용자가 존재하지 않음")
    void refreshToken_Fail_UserNotFound() {
        // given
        String validRefreshToken = "valid.refresh.token";
        String email = "nonexistent@example.com";

        given(jwtTokenProvider.validateToken(validRefreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(validRefreshToken)).willReturn(email);
        given(userRepository.findActiveUserByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(validRefreshToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(jwtTokenProvider, never()).createAccessToken(anyString(), anyString());
    }

    @Test
    @DisplayName("실패 8: 토큰 재발급 시 삭제된 사용자의 토큰")
    void refreshToken_Fail_DeletedUserToken() {
        // given
        String validRefreshToken = "valid.refresh.token";
        String email = "deleted@example.com";

        given(jwtTokenProvider.validateToken(validRefreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(validRefreshToken)).willReturn(email);
        given(userRepository.findActiveUserByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(validRefreshToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 9: Null 이메일로 회원가입 시도")
    void signUp_Fail_NullEmail() {
        // given
        SignUpRequest request = new SignUpRequest(
                null,
                "Test1234!",
                "테스터"
        );

        // when & then
        // Validation은 Controller 레벨에서 처리되므로,
        // 서비스 레벨에서는 NPE 발생 가능
        given(userRepository.existsByEmail(null)).willThrow(new IllegalArgumentException());

        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("실패 10: 데이터베이스 저장 실패")
    void signUp_Fail_DatabaseError() {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@example.com",
                "Test1234!",
                "테스터"
        );

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByNickname(request.getNickname())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class)))
                .willThrow(new RuntimeException("Database connection failed"));

        // when & then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
    }

    @Test
    @DisplayName("성공: 정상적인 회원가입")
    void signUp_Success() {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@example.com",
                "Test1234!",
                "테스터"
        );

        User savedUser = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(userRepository.existsByNickname(request.getNickname())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        UserResponse response = authService.signUp(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getNickname()).isEqualTo(request.getNickname());
        assertThat(response.getRole()).isEqualTo(Role.USER);

        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByNickname(request.getNickname());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("성공: 정상적인 로그인")
    void login_Success() {
        // given
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "Test1234!"
        );

        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("테스터")
                .role(Role.USER)
                .build();

        given(userRepository.findActiveUserByEmail(request.getEmail()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .willReturn(true);
        given(jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().getKey()))
                .willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(user.getEmail()))
                .willReturn("refreshToken");
        given(jwtTokenProvider.getExpiration("accessToken")).willReturn(86400000L);

        // when
        TokenResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86400000L);

        verify(userRepository).findActiveUserByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtTokenProvider).createAccessToken(user.getEmail(), user.getRole().getKey());
        verify(jwtTokenProvider).createRefreshToken(user.getEmail());
    }

    @Test
    @DisplayName("성공: 정상적인 토큰 재발급")
    void refreshToken_Success() {
        // given
        String refreshToken = "valid.refresh.token";
        String email = "test@example.com";

        User user = User.builder()
                .email(email)
                .password("encodedPassword")
                .nickname("테스터")
                .role(Role.USER)
                .build();

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getEmailFromToken(refreshToken)).willReturn(email);
        given(userRepository.findActiveUserByEmail(email)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createAccessToken(email, user.getRole().getKey()))
                .willReturn("newAccessToken");
        given(jwtTokenProvider.createRefreshToken(email)).willReturn("newRefreshToken");
        given(jwtTokenProvider.getExpiration("newAccessToken")).willReturn(86400000L);

        // when
        TokenResponse response = authService.refreshToken(refreshToken);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(jwtTokenProvider).validateToken(refreshToken);
        verify(jwtTokenProvider).getEmailFromToken(refreshToken);
        verify(userRepository).findActiveUserByEmail(email);
        verify(jwtTokenProvider).createAccessToken(email, user.getRole().getKey());
        verify(jwtTokenProvider).createRefreshToken(email);
    }
}

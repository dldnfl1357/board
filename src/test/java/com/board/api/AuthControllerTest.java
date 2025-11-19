package com.board.api;

import com.board.common.RestDocsTestSupport;
import com.board.domain.user.dto.LoginRequest;
import com.board.domain.user.dto.SignUpRequest;
import com.board.domain.user.entity.Role;
import com.board.domain.user.entity.User;
import com.board.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("인증 API 테스트")
class AuthControllerTest extends RestDocsTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest(
                "test@example.com",
                "Test1234!",
                "테스터"
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("테스터"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andDo(document("auth/signup",
                        requestFields(
                                fieldWithPath("email").description("이메일 (이메일 형식)"),
                                fieldWithPath("password").description("비밀번호 (8-20자, 영문/숫자/특수문자 포함)"),
                                fieldWithPath("nickname").description("닉네임 (2-20자)")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.id").description("사용자 ID"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.role").description("역할 (USER, ADMIN)"),
                                fieldWithPath("data.createdAt").description("생성 일시"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("Test1234!")
                .nickname("테스터")
                .role(Role.USER)
                .build();
        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        LoginRequest request = new LoginRequest("test@example.com", "Test1234!");

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").exists())
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andDo(document("auth/login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.accessToken").description("액세스 토큰 (유효기간: 24시간)"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰 (유효기간: 7일)"),
                                fieldWithPath("data.tokenType").description("토큰 타입"),
                                fieldWithPath("data.expiresIn").description("토큰 만료 시간 (밀리초)"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void refreshToken_Success() throws Exception {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("Test1234!")
                .nickname("테스터")
                .role(Role.USER)
                .build();
        user.encodePassword(passwordEncoder);
        userRepository.save(user);

        // 먼저 로그인하여 토큰 발급
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Test1234!");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = objectMapper.readTree(loginResponse)
                .path("data")
                .path("refreshToken")
                .asText();

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                .header("Refresh-Token", refreshToken));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.message").value("토큰이 재발급되었습니다."))
                .andDo(document("auth/refresh",
                        requestHeaders(
                                headerWithName("Refresh-Token").description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("success").description("성공 여부"),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("data.accessToken").description("새로운 액세스 토큰"),
                                fieldWithPath("data.refreshToken").description("새로운 리프레시 토큰"),
                                fieldWithPath("data.tokenType").description("토큰 타입"),
                                fieldWithPath("data.expiresIn").description("토큰 만료 시간 (밀리초)"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )
                ));
    }
}

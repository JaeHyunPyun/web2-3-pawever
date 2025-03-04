package com.pawever.server.domain.user.controller;


import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.response.UserResponseDto;
import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest(classes = {PawEverApplication.class})
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
class RefreshTokenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity()) // Spring Security 적용
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    private HeadersModifyingOperationPreprocessor getModifiedHeader() {
        return modifyHeaders().remove("X-Content-Type-Options")
            .remove("X-XSS-Protection")
            .remove("Cache-Control")
            .remove("Pragma")
            .remove("Expires")
            .remove("Content-Length");
    }

    // 응답 필드 설명(Refresh Token 갱신 실패 시)
    private final List<FieldDescriptor> responseFieldDescriptorsForFailure = List.of(
        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부 (false)"),
        fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 에러 코드"),
        fieldWithPath("data").type(JsonFieldType.OBJECT).optional().description("에러 발생 시 추가 데이터")
    );

    private ResultActions getResultActionsForRefreshToken(String refreshToken) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/api/auth/refreshedtokens")
                .cookie(new Cookie("refresh", refreshToken)) // Refresh Token 쿠키 추가
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private ResultActions getResultActionsForRefreshToken() throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/api/auth/refreshedtokens") // 쿠키 없이 요청
                .cookie(new Cookie("object", "object"))
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private RestDocumentationResultHandler getDocumentForRefreshToken(String identifier, boolean isSuccess) {
        return document(
            "api/auth/refreshedtokens/" + identifier,
            preprocessRequest(prettyPrint(), modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
            preprocessResponse(prettyPrint(), getModifiedHeader()),
            responseHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer: New Access Token").optional()
            ),
            responseCookies(
                cookieWithName("refresh").description("New Refresh Token (HttpOnly)").optional()
            ),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("인증-auth")
                    .summary("Jwt Token 갱신 API")
                    .description(
                        "기존 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급하는 API.\n\n" +
                            (isSuccess ? "성공 시 응답 본문이 없고, 204 No Content를 반환합니다.(헤더를 통해 토큰 전달)" :
                                "실패 시 예외 코드와 응답 본문을 반환합니다.")
                    )
                    .responseFields(isSuccess ? Collections.emptyList() : responseFieldDescriptorsForFailure)
                    .build())
        );
    }

    // Refresh Token 갱신 성공 테스트
    @Test
    @Transactional
    void refreshTokenSuccessTest() throws Exception {
        // Given
        // 1. 테스트용 refresh토큰 생성
        UserResponseDto userResponseDto = UserResponseDto.builder()
            .userId(100)
            .socialLoginUuid("800a8d7a-442d-4f5b-967e-2ed138f6e789")
            .name("TokenRefreshTester")
            .role(Role.ROLE_USER)
            .build();

        String testRefreshToken = jwtUtil.createJwt("refresh", userResponseDto, 1000L * 60 * 60 * 24); // 24시간 유지

        // 2. 생성된 refresh 토큰을 서버 redis에 저장
        refreshTokenService.saveRefreshToken(testRefreshToken, userResponseDto.getName());

        // When
        ResultActions resultActions = getResultActionsForRefreshToken(testRefreshToken);

        // Then
        resultActions.andExpect(status().isNoContent()) // 204 응답 확인
            .andExpect(content().string("")) // Response Body 없음 확인
            .andExpect(header().exists(HttpHeaders.AUTHORIZATION)) // 새로운 Access Token 존재 확인
            .andExpect(cookie().exists("refresh")); // 새로운 Refresh Token 존재 여부 확인

        // Documentation
        resultActions.andDo(getDocumentForRefreshToken("success", true));
    }

    // Refresh Token 갱신 실패 테스트 - 리프레시토큰이 없는 경우 (400)
    @Test
    @Transactional
    void refreshTokenFailureTestNullToken() throws Exception {
        // When
        ResultActions resultActions = getResultActionsForRefreshToken();

        // Then
        resultActions.andExpect(status().isBadRequest()) // 400 응답 확인
            .andExpect(jsonPath("isSuccess").value(false))
            .andExpect(jsonPath("status").value(ResponseCodeEnum.REFRESH_TOKEN_NULL.getStatus().name()))
            .andExpect(jsonPath("code").value(ResponseCodeEnum.REFRESH_TOKEN_NULL.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForRefreshToken("failure-null", false));
    }


    // Refresh Token 갱신 실패 테스트 - 만료된 토큰 (401)
    @Test
    @Transactional
    void refreshTokenFailureTestExpiredToken() throws Exception {
        // Given
        UserResponseDto userResponseDto = UserResponseDto.builder()
            .userId(50)
            .socialLoginUuid("900a8d7a-442d-4f5b-967e-2ed138f6e456")
            .name("EXPIREDTokenRefreshTester")
            .role(Role.ROLE_ADMIN)
            .build();

        String expiredTestRefreshToken = jwtUtil.createJwt("refresh", userResponseDto, 0L); // 만료된 토큰 생성

        // When
        ResultActions resultActions = getResultActionsForRefreshToken(expiredTestRefreshToken);

        // Then
        resultActions.andExpect(status().isUnauthorized()) // 401 응답 확인
            .andExpect(jsonPath("isSuccess").value(false))
            .andExpect(jsonPath("status").value(ResponseCodeEnum.JWT_TOKEN_EXPIRED.getStatus().name()))
            .andExpect(jsonPath("code").value(ResponseCodeEnum.JWT_TOKEN_EXPIRED.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForRefreshToken("failure-expired", false));
    }

    // Refresh Token 갱신 실패 테스트 - accesstoken을 보낸 경우(400)
    @Test
    @Transactional
    void refreshTokenFailureTestCategoryMismatch() throws Exception {
        // Given
        // 토큰 생성후 서버 Redis에 넣지 않음
        UserResponseDto userResponseDto = UserResponseDto.builder()
            .userId(35)
            .socialLoginUuid("445a8d7a-442d-4f5b-967e-2ed138f6e542")
            .name("MismatchTokenRefreshTester")
            .role(Role.ROLE_STAFF)
            .build();

        // access 토큰 생성해서 request에 담아 보냄
        String categoryMistmatchTestRefreshToken = jwtUtil.createJwt("access", userResponseDto,
            1000L * 60 * 60 * 24);

        // When
        ResultActions resultActions = getResultActionsForRefreshToken(categoryMistmatchTestRefreshToken);

        // Then
        resultActions.andExpect(status().isBadRequest()) // 400 응답 확인
            .andExpect(jsonPath("isSuccess").value(false))
            .andExpect(jsonPath("status").value(
                ResponseCodeEnum.TOKEN_CATEGORY_MISMATCH.getStatus().name()))
            .andExpect(jsonPath("code").value(ResponseCodeEnum.TOKEN_CATEGORY_MISMATCH.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForRefreshToken("failure-mismatch", false));
    }

    // Refresh Token 갱신 실패 테스트 - 서버에 없는 토큰 (403)
    @Test
    @Transactional
    void refreshTokenFailureTestStolenToken() throws Exception {
        // Given
        // 토큰 생성후 서버 Redis에 넣지 않음
        UserResponseDto userResponseDto = UserResponseDto.builder()
            .userId(75)
            .socialLoginUuid("300a8d7a-442d-4f5b-967e-2ed138f6e333")
            .name("StolenTokenRefreshTester")
            .role(Role.ROLE_STAFF)
            .build();

        String stolenTestRefreshToken = jwtUtil.createJwt("refresh", userResponseDto, 1000L * 60 * 60 * 24); // 만료된 토큰 생성

        // When
        ResultActions resultActions = getResultActionsForRefreshToken(stolenTestRefreshToken);

        // Then
        resultActions.andExpect(status().isUnauthorized()) // 401 응답 확인
            .andExpect(jsonPath("isSuccess").value(false))
            .andExpect(jsonPath("status").value(ResponseCodeEnum.REFRESH_TOKEN_NOT_FOUND.getStatus().name()))
            .andExpect(jsonPath("code").value(ResponseCodeEnum.REFRESH_TOKEN_NOT_FOUND.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForRefreshToken("failure-stolen", false));
    }
}

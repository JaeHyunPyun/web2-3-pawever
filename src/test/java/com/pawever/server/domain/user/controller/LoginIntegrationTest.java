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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.user.dto.request.AuthRequestDto;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

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

    // 요청 필드 설명 (Request Body를 통해 받음)
    private final List<FieldDescriptor> requestFieldDescriptorsForLogin = List.of(
        fieldWithPath("socialLoginUuid").type(JsonFieldType.STRING).optional().description("소셜 로그인 UUID (필수값이나, 실패테스트에 null값을 넣기 위해 optional을 사용했습니다.)"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("소셜 로그인 닉네임 (필수)"),
        fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).optional().description("프로필 이미지 URL (선택)"),
        fieldWithPath("email").type(JsonFieldType.STRING).description("소셜 로그인 이메일 (필수)"),
        fieldWithPath("socialLoginProvider").type(JsonFieldType.STRING).description("소셜 로그인 제공자 (카카오 또는 구글) (필수)"),
        fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("사용자 위치 - 위도 (필수)"),
        fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("사용자 위치 - 경도 (필수)")
    );

    // 응답 필드 설명(로그인 실패시)
    private final List<FieldDescriptor> responseFieldDescriptorsForFailure = List.of(
        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부 (false)"),
        fieldWithPath("status").type(JsonFieldType.STRING).description("HTTP 상태 코드"),
        fieldWithPath("code").type(JsonFieldType.STRING).description("응답 에러 코드"),
        fieldWithPath("data").type(JsonFieldType.OBJECT).optional().description("에러 발생 시 추가 데이터")
    );

    // 실제 API Request 수행
    private ResultActions getResultActionsForLogin(AuthRequestDto authRequestDto) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/api/auth/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authRequestDto))
        );
    }

    private RestDocumentationResultHandler getDocumentForLogin(String identifier, boolean isSuccess) {
        return document(
            "api/auth/login/" + identifier,
            preprocessRequest(prettyPrint(), modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
            preprocessResponse(prettyPrint(), getModifiedHeader()),
            requestFields(requestFieldDescriptorsForLogin), // 요청 JSON 필드 문서화
            responseHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer: Access Token").optional() // 성공 시만 존재
            ),
            responseCookies(
                cookieWithName("refresh").description("Refresh Token (HttpOnly)").optional() // 성공 시만 존재
            ),
            resource(
                ResourceSnippetParameters.builder()
                    .tag("인증-auth")
                    .summary("사용자 로그인 API")
                    .description(
                        "프론트에서 소셜 로그인 후 전달하는 유저 정보를 받아 AccessToken과 RefreshToken을 반환하는 API.\n\n" +
                            (isSuccess ? "성공 시 응답 본문이 없고, 204 No Content를 반환합니다." :
                                "실패 시 예외코드와 응답 본문을 반환합니다.")
                    )
                    .responseFields(isSuccess ? Collections.emptyList() : responseFieldDescriptorsForFailure) // 실패 시만 응답 필드 추가
                    .build())
        );
    }


    // 로그인 성공 테스트
    @Test
    @Transactional
    void loginSuccessTest() throws Exception {
        // Given
        AuthRequestDto authRequestDto = new AuthRequestDto(
            "example-uuid",
            "John Doe",
            "https://example.com/profile.jpg",
            "johndoe@example.com",
            "kakao",
            new BigDecimal("37.5665"),
            new BigDecimal("126.9780")
        );

        // When
        ResultActions resultActions = getResultActionsForLogin(authRequestDto);

        // Then
        resultActions.andExpect(status().isNoContent()) // 204 응답 확인
            .andExpect(content().string("")) // Response Body 없음 확인
            .andExpect(header().exists(HttpHeaders.AUTHORIZATION)) // Authorization 헤더 존재 확인
            .andExpect(cookie().exists("refresh")); // Refresh Token 쿠키 존재 여부 확인

        // Documentation
        resultActions.andDo(getDocumentForLogin("success", true));
    }

    // 로그인 실패 테스트 - 회원 정보 저장 실패 (MISSING_REQUIRED_FIELDS) (400)
    @Test
    @Transactional
    void loginFailureTestMissingRequiredFields() throws Exception {
        // Given
        // 필수값인 socialLoginUuid에 null 부여
        AuthRequestDto authRequestDto = new AuthRequestDto(
            null,
            "John Doe",
            null,
            "johndoe@example.com",
            "kakao",
            new BigDecimal("37.5665"),
            new BigDecimal("126.9780")
        );

        // When
        ResultActions resultActions = getResultActionsForLogin(authRequestDto);

        // Then
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("isSuccess").value(false))
            .andExpect(jsonPath("status").value(ResponseCodeEnum.MISSING_REQUIRED_FIELDS.getStatus().name()))
            .andExpect(jsonPath("code").value(ResponseCodeEnum.MISSING_REQUIRED_FIELDS.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForLogin("failure-missing-fields", false));
    }

}

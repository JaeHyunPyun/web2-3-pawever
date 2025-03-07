package com.pawever.server.domain.recommendation.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.recommendation.dto.recommendation.RecommendationRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Sql("classpath:recommendation_data.sql")
@SpringBootTest(classes = {PawEverApplication.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
public class PetRecommendationControllerTest {
    /*

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                // .apply(SecurityMockMvcConfigurers.springSecurity()) // 필요시 활성화
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    // 불필요한 header 제거 함수
    private HeadersModifyingOperationPreprocessor getModifiedHeader() {
        return modifyHeaders()
                .remove("X-Content-Type-Options")
                .remove("X-XSS-Protection")
                .remove("Cache-Control")
                .remove("Pragma")
                .remove("Expires")
                .remove("Content-Length");
    }

    // 요청 필드 설명
    private final List<FieldDescriptor> requestFieldDescriptors = List.of(
            fieldWithPath("responses").type(JsonFieldType.OBJECT).description("사용자의 질문 응답 맵 (질문ID: 선택옵션ID)"),
            fieldWithPath("responses.*").type(JsonFieldType.NUMBER).description("각 질문에 대한 응답 값 (옵션ID)")
    );

    // 응답 필드 설명
    private final List<FieldDescriptor> responseFieldDescriptors = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            fieldWithPath("data").type(JsonFieldType.ARRAY).description("추천 반려동물 목록"),
            fieldWithPath("data[].recommendPetId").type(JsonFieldType.NUMBER).description("추천 반려동물 ID"),
            fieldWithPath("data[].breed").type(JsonFieldType.STRING).description("견종명(영문)"),
            fieldWithPath("data[].breedKor").type(JsonFieldType.STRING).description("견종명(한글)"),
            fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("견종 이미지 URL"),
            fieldWithPath("data[].temperament").type(JsonFieldType.STRING).description("성격 특징"),
            fieldWithPath("data[].lifespan").type(JsonFieldType.STRING).description("평균 수명"),
            fieldWithPath("data[].precaution").type(JsonFieldType.STRING).description("주의사항")
    );

    // 실제 API 요청 수행
    private ResultActions getResultActionsForRecommendation(RecommendationRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders
                        .post("/api/recommend-animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        );
    }

    // 문서화 반환 함수
    private RestDocumentationResultHandler getDocumentForRecommendation(String identifier) {
        return document(
                "api/recommend-animals/" + identifier,
                preprocessRequest(prettyPrint(), modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
                preprocessResponse(prettyPrint(), getModifiedHeader()),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors),
                resource(
                        ResourceSnippetParameters.builder()
                                .tag("추천-recommendation")
                                .summary("반려동물 추천 API")
                                .description("사용자의 질문 응답을 기반으로 적합한 반려동물 견종을 추천합니다.")
                                .build()
                )
        );
    }

    @Test
    @Transactional
    void recommendAnimalsSuccessTest() throws Exception {
        // Given
        Map<Integer, Integer> responses = new HashMap<>();

        //전체1 : 푸들
        responses.put(1, 1);
        responses.put(2, 1);
        responses.put(3, 1);
        responses.put(4, 1);
        responses.put(5, 1);
        responses.put(6, 1);
        responses.put(7, 1);
        responses.put(8, 1);
        responses.put(9, 1);
        responses.put(10, 1);
        responses.put(11, 1);
        responses.put(12, 1);
        responses.put(13, 1);
        responses.put(14, 1);
        responses.put(15, 1);
        responses.put(16, 1);
        responses.put(17, 1);
        responses.put(18, 1);
        responses.put(19, 1);
        responses.put(20, 1);

        RecommendationRequest request = new RecommendationRequest();
        request.setResponses(responses);

        // When
        ResultActions resultActions = getResultActionsForRecommendation(request);

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("status").value(ResponseCodeEnum.SUCCESS.getStatus().name()))
                .andExpect(jsonPath("code").value(ResponseCodeEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("data[0].breed").exists())
                .andExpect(jsonPath("data[0].breed").value("Poodle"))
                .andExpect(jsonPath("data[0].breedKor").exists());

        // Documentation
        resultActions.andDo(getDocumentForRecommendation("success"));
    }

    @Test
    @Transactional
    void recommendAnimalsWithInvalidOptionValueTest() throws Exception {

        // Given - 유효범위를 벗어난 옵션 값 (예: 0이나 6)
        Map<Integer, Integer> responses = new HashMap<>();
        // 일부 질문에 유효하지 않은 값 설정
        responses.put(1, 0);  // 범위 밖의 하한값
        responses.put(2, 6);  // 범위 밖의 상한값
        responses.put(3, 1);  // 유효한 값
        responses.put(4, 1);  // 유효한 값
        responses.put(5, 1);  // 유효한 값
        responses.put(6, 1);
        responses.put(7, 1);
        responses.put(8, 1);
        responses.put(9, 1);
        responses.put(10, 1);
        responses.put(11, 1);
        responses.put(12, 1);
        responses.put(13, 1);
        responses.put(14, 1);
        responses.put(15, 1);
        responses.put(16, 1);
        responses.put(17, 1);
        responses.put(18, 1);
        responses.put(19, 1);
        responses.put(20, 1);

        RecommendationRequest request = new RecommendationRequest();
        request.setResponses(responses);

        // When
        ResultActions resultActions = getResultActionsForRecommendation(request);

        // Then - 서비스가 이런 입력도 적절히 처리하는지 확인
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("status").value(ResponseCodeEnum.SUCCESS.getStatus().name()))
                .andExpect(jsonPath("code").value(ResponseCodeEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("data[0].breed").value("Poodle"))
                .andExpect(jsonPath("data[0].breed").exists());

        // Documentation
        resultActions.andDo(getDocumentForRecommendation("invalid-option-values"));
    }

    @Test
    @Transactional
    void recommendAnimalsWithMissingQuestionsTest() throws Exception {
        // Given - 일부 질문만 포함된 응답
        Map<Integer, Integer> responses = new HashMap<>();

        responses.put(1, 1);
        responses.put(5, 1);
        responses.put(10, 1);
        responses.put(15, 1);
        responses.put(20, 1);

        RecommendationRequest request = new RecommendationRequest();
        request.setResponses(responses);

        // When
        ResultActions resultActions = getResultActionsForRecommendation(request);

        // Then - 서비스가 이런 입력도 적절히 처리하는지 확인
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("status").value(ResponseCodeEnum.SUCCESS.getStatus().name()))
                .andExpect(jsonPath("code").value(ResponseCodeEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("data[0].breed").exists());

        // Documentation
        resultActions.andDo(getDocumentForRecommendation("missing-questions"));
    }


     */

}
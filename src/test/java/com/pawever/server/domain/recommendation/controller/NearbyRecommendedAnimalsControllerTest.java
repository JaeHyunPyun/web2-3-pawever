package com.pawever.server.domain.recommendation.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;

import com.pawever.server.domain.recommendation.dto.nearby.NearbyRecommendedAnimalsRequest;
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
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:recommendation_data.sql")
@SpringBootTest(classes = {PawEverApplication.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
public class NearbyRecommendedAnimalsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                //.apply(SecurityMockMvcConfigurers.springSecurity()) // Security 설정 완료 후 적용
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
            fieldWithPath("recommendedBreed").type(JsonFieldType.STRING).description("추천된 견종 이름"),
            fieldWithPath("userLatitude").type(JsonFieldType.NUMBER).description("사용자의 위도"),
            fieldWithPath("userLongitude").type(JsonFieldType.NUMBER).description("사용자의 경도")
    );


    // 응답 필드 설명
    private final List<FieldDescriptor> responseFieldDescriptors = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            fieldWithPath("data").type(JsonFieldType.ARRAY).description("주변 추천 동물 목록"),
            fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("동물 이미지 URL"),
            fieldWithPath("data[].name").type(JsonFieldType.STRING).description("동물 이름"),
            fieldWithPath("data[].age").type(JsonFieldType.STRING).description("동물 나이"),
            fieldWithPath("data[].sex").type(JsonFieldType.STRING).description("성별"),
            fieldWithPath("data[].shelterName").type(JsonFieldType.STRING).description("보호소 이름"),
            fieldWithPath("data[].distanceKm").type(JsonFieldType.NUMBER).description("사용자로부터의 거리(km)")
    );

    // 결과가 없는 경우의 응답 필드 설명
    private final List<FieldDescriptor> emptyResponseFieldDescriptors = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            fieldWithPath("data").type(JsonFieldType.ARRAY).description("주변 추천 동물 목록 (빈 배열)")
    );

    @Test
    void getNearbyRecommendedAnimalsTest_Success() throws Exception {
        // 실제 DB에 이미 있는 데이터를 조회하는 테스트
        NearbyRecommendedAnimalsRequest request = NearbyRecommendedAnimalsRequest.builder()
                .recommendedBreed("푸들")  // DB에 존재하는 품종으로 지정
                .userLatitude(37.5665)
                .userLongitude(126.9780)
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/recommend-animals/nearby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("api/recommend-animals/nearby",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        requestFields(requestFieldDescriptors),
                        responseFields(responseFieldDescriptors),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("추천-recommendation")
                                        .summary("주변 추천 동물 조회 API")
                                        .description("사용자 위치와 추천 견종을 기반으로 근처 보호소에 있는 추천 동물 목록을 반환합니다. " +
                                                "가장 가까운 보호소부터 최대 4마리의 동물을 반환합니다.")
                                        .build())
                ));
    }

    @Test
    void getNearbyRecommendedAnimalsTest_NoResults() throws Exception {
        // 존재하지 않는 품종으로 요청
        NearbyRecommendedAnimalsRequest request = NearbyRecommendedAnimalsRequest.builder()
                .recommendedBreed("존재하지않는견종")
                .userLatitude(37.5665)
                .userLongitude(126.9780)
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/api/recommend-animals/nearby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("api/recommend-animals/nearby-empty",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        requestFields(requestFieldDescriptors),
                        responseFields(emptyResponseFieldDescriptors),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("추천-recommendation")
                                        .summary("주변 추천 동물 조회 API (결과 없음)")
                                        .description("검색 조건에 맞는 동물이 없는 경우 빈 배열을 반환합니다.")
                                        .build())
                ));
    }
}
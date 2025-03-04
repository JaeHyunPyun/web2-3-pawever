package com.pawever.server.domain.carehub.controller;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.enums.NeuteredStatus;
import com.pawever.server.domain.carehub.enums.Sex;
import com.pawever.server.domain.carehub.enums.Species;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;

import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.restdocs.request.ParameterDescriptor;
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
class AbandonedPetDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AbandonedPetRepository abandonedPetRepository;

    @Autowired
    private ShelterRepository shelterRepository;



    @BeforeEach
    void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
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

    // 응답 필드 설명
    private final List<FieldDescriptor> responseFieldDescriptorsForPetDetail = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("유기동물 ID"),
            fieldWithPath("data.name").type(JsonFieldType.STRING).description("유기동물 이름"),
            fieldWithPath("data.neuteredStatus").type(JsonFieldType.STRING).description("중성화 상태"),
            fieldWithPath("data.weight").type(JsonFieldType.STRING).description("체중").optional(),
            fieldWithPath("data.color").type(JsonFieldType.STRING).description("색상").optional(),
            fieldWithPath("data.characteristics").type(JsonFieldType.STRING).description("특징").optional(),
            fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL").optional(),
            fieldWithPath("data.shelterName").type(JsonFieldType.STRING).description("보호소 이름"),
            fieldWithPath("data.shelterPhoneNumber").type(JsonFieldType.STRING).description("보호소 전화번호"),
            fieldWithPath("data.shelterRoadAddress").type(JsonFieldType.STRING).description("보호소 도로명 주소"),
            fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("보호소 위도"),
            fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("보호소 경도")
    );

    // 실패 응답 필드 설명
    private final List<FieldDescriptor> responseFieldDescriptorsForFailure = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부 (false)"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("응답 에러 코드"),
            fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (null)")
    );

    // Path 파라미터 설명
    private final List<ParameterDescriptor> pathParametersForPetDetail = List.of(
            parameterWithName("petId").description("유기동물 ID")
    );

    // 실제 API Request 수행
    private ResultActions getResultActionsForPetDetail(Long petId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/api/animals/{petId}", petId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    // 문서화 헬퍼 함수
    private RestDocumentationResultHandler getDocumentForPetDetail(String identifier, boolean isSuccess) {
        return document(
                "api/animals/" + identifier,
                preprocessRequest(prettyPrint(), modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
                preprocessResponse(prettyPrint(), getModifiedHeader()),
                pathParameters(pathParametersForPetDetail),
                responseFields(isSuccess ? responseFieldDescriptorsForPetDetail : responseFieldDescriptorsForFailure),
                resource(
                        ResourceSnippetParameters.builder()
                                .tag("유기동물-carehub")
                                .summary("유기동물 상세 정보 조회 API")
                                .description(
                                        "유기동물 ID를 기반으로 해당 유기동물의 상세 정보를 조회하는 API.\n\n" +
                                                (isSuccess ? "성공 시 유기동물의 상세 정보와 보호소 정보를 반환합니다." :
                                                        "실패 시 예외코드와 응답 본문을 반환합니다.")
                                )
                                .build()
                )
        );
    }

    // 유기동물 상세 조회 성공 테스트
    @Test
    @Transactional
    void getAbandonedPetDetailSuccessTest() throws Exception {
        // Given
        // 보호소 데이터 생성
        Shelter testShelter = shelterRepository.save(Shelter.builder()
                .name("보호소")
                .centerPhoneNumber("02-123-4567")
                .roadAddress("서울시 강남구 테헤란로 123")
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .providerShelterId(1234L)
                .cityCode("11")
                .districtCode("22")
                .build());

        // 유기동물 데이터 생성
        AbandonedPet testPet = abandonedPetRepository.save(AbandonedPet.builder()
                .id(1L)
                .name("멍멍이")
                .neuteredStatus(NeuteredStatus.Y)
                .weight("5.5")
                .color("갈색")
                .characteristics("활발하고 친근함")
                .imageUrl("https://example.com/dog.jpg")
                .providerShelterId(testShelter.getProviderShelterId())
                .species(Species.DOG)
                .breed("믹스")
                .sex(Sex.M)
                .age("2")
                .foundLocation("서울시 강남구")
                .noticeNumber("서울-강남-2025-00001")
                .build());

        Long petId = testPet.getId();

        // When
        ResultActions resultActions = getResultActionsForPetDetail(petId);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("status").value(ResponseCodeEnum.SUCCESS.getStatus().name()))
                .andExpect(jsonPath("code").value(ResponseCodeEnum.SUCCESS.getCode()))
                .andExpect(jsonPath("data.id").value(petId))
                .andExpect(jsonPath("data.name").value("멍멍이"));

        // Documentation
        resultActions.andDo(getDocumentForPetDetail("success", true));
    }

    // 유기동물 상세 조회 실패 테스트 - 존재하지 않는 유기동물
    @Test
    @Transactional
    void getAbandonedPetDetailFailureAnimalNotFound() throws Exception {
        // Given
        Long nonExistentPetId = 9999L;

        // When
        ResultActions resultActions = getResultActionsForPetDetail(nonExistentPetId);

        // Then
        resultActions.andExpect(status().isNotFound())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("status").value(ResponseCodeEnum.ANIMAL_NOT_FOUND.getStatus().name()))
                .andExpect(jsonPath("code").value(ResponseCodeEnum.ANIMAL_NOT_FOUND.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForPetDetail("failure-animal-not-found", false));
    }

    // 유기동물 상세 조회 실패 테스트 - 보호소 정보 누락
    @Test
    @Transactional
    void getAbandonedPetDetailFailureShelterNotFound() throws Exception {
        // Given
        // 보호소 ID를 다르게 설정하여 보호소를 찾을 수 없게 함
        AbandonedPet petWithoutShelter = abandonedPetRepository.save(AbandonedPet.builder()
                .id(2L)
                .name("길냥이")
                .neuteredStatus(NeuteredStatus.N)
                .weight("3.2")
                .color("검정")
                .characteristics("조용하고 온순함")
                .imageUrl("https://example.com/cat.jpg")
                .providerShelterId(9999L) // 존재하지 않는 보호소 ID
                .species(Species.CAT)
                .breed("코리안숏헤어")
                .sex(Sex.F)
                .age("1")
                .foundLocation("서울시 강북구")
                .noticeNumber("서울-강북-2025-00001")
                .build());

        Long petId = petWithoutShelter.getId();

        // When
        ResultActions resultActions = getResultActionsForPetDetail(petId);

        // Then
        resultActions.andExpect(status().isNotFound())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("status").value(ResponseCodeEnum.SHELTER_NOT_FOUND.getStatus().name()))
                .andExpect(jsonPath("code").value(ResponseCodeEnum.SHELTER_NOT_FOUND.getCode()));

        // Documentation
        resultActions.andDo(getDocumentForPetDetail("failure-shelter-not-found", false));
    }
}
package com.pawever.server.domain.reservation.integration;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.config.TestExecutionListener;
import com.pawever.server.domain.reservation.dto.out.ReservationTimeResponseDto;
import com.pawever.server.domain.reservation.service.ReservationTimeSlotService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest(classes = {PawEverApplication.class})
@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
@TestExecutionListeners(value = TestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationTimeSlotService reservationTimeSlotService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext context , RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                //.apply(SecurityMockMvcConfigurers.springSecurity()) //Security 필터 적용 후 다시 적용
                .apply(documentationConfiguration(restDocumentation))
                .build();

    }

    // 블필요한 header 제거 함수
    private HeadersModifyingOperationPreprocessor getModifiedHeader() {
        return modifyHeaders().remove("X-Content-Type-Options").remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma").remove("Expires").remove("Content-Length");
    }

    private final List<FieldDescriptor> responseFieldDescriptorsForReservationTimeCheck = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            fieldWithPath("data").type(JsonFieldType.OBJECT).optional().description("data"),
            fieldWithPath("data.isShelterReservationAvailable").type(JsonFieldType.BOOLEAN).description("우리 서비스를 통한 방문 예약이 가능한지 여부. 불가능할 경우 전화 예약"),
            fieldWithPath("data.reservationTimeList").type(JsonFieldType.ARRAY).description("보호소 방문 가능 시간대 나열").attributes(new Attributes.Attribute("constraint","예약이 불가능한 보호소의 경우 null")),
            fieldWithPath("data.reservationTimeList[].time").type(JsonFieldType.STRING).description("보소호 방문 시간대"),
            fieldWithPath("data.reservationTimeList[].isEnabled").type(JsonFieldType.BOOLEAN).description("해당 시간대 방문 예약 가능 여부")

    );

    private final List<ParameterDescriptor> queryParametersForReservationTimeCheck = List.of(
            parameterWithName("date").description("보호소 방문 날짜").attributes(new Attributes.Attribute("constraint","YYYY-MM-DD"))
    );

    private final List<ParameterDescriptor> pathParametersForReservationTimeCheck = List.of(
            parameterWithName("shelter_id").description("보호소 고유 id")
    );



    private ResultActions getResultActionsForReservationTimeCheck(Integer shelterId) throws Exception {
        return mockMvc.perform( // api 실행
                RestDocumentationRequestBuilders
                        .get("/api/reservations/shelters/{shelter_id}",shelterId)
                        .param("date","2025-02-03")
                        //.header(HttpHeaders.AUTHORIZATION, "Bearer "+token) //jwt 설정 완료 후 도입
        );
    }

    //문서화 반환 함수
    private RestDocumentationResultHandler getDocumentForReservationTimeCheck(Integer identifier){
        return document("api/reservations/shelters/"+identifier,
                preprocessRequest(prettyPrint(),modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),   // host 이름 변경
                preprocessResponse(prettyPrint(), getModifiedHeader()),  // (3)
                responseFields(responseFieldDescriptorsForReservationTimeCheck), // response body field descriptor
                queryParameters(queryParametersForReservationTimeCheck), //query parameter descriptor
                pathParameters(pathParametersForReservationTimeCheck),
                //requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer : 사용자 access Token")), //jwt 토큰, security 설정 이후 활성화
                resource(
                        ResourceSnippetParameters.builder()
                                .tag("에약-reservation") // 문서에서 api들이 태그로 분류됨 -> 도메인 이름으로 분류하면 좋을 듯.
                                .summary("보호소 별 방문 예약 가능 시간대 조회 api") // api 이름
                                .description("보호소 별 날짜에 따른 방문 예약 가능 시간대를 조회함.") // api 상세 설명
                                .build()));
    }

    @Test
    @Transactional
    public void 보호소_예약시간_조회_성공() throws Exception {

        //given
        reservationTimeSlotService.createReservationTimeSlotForShelter(1L);

        //when
        ResultActions resultActions = getResultActionsForReservationTimeCheck(1);

        //then
        resultActions.andExpect(status().isOk());

        //documentation
        resultActions.andDo(getDocumentForReservationTimeCheck(1));

    }

    @Test
    @Transactional
    public void p보호소_예약시간_조회_실패1() throws Exception {

        //given

        //when
        ResultActions resultActions = getResultActionsForReservationTimeCheck(10000);

        //then
        resultActions.andExpect(status().is(ResponseCodeEnum.SHELTER_NOT_FOUND.getStatus().value())).andExpect(jsonPath("code").value(ResponseCodeEnum.SHELTER_NOT_FOUND.getCode()));

        //documentation
        resultActions.andDo(getDocumentForReservationTimeCheck(2));

    }

}

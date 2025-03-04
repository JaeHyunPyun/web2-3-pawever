package com.pawever.server.domain.donation.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.donation.entity.Donation;
import com.pawever.server.domain.donation.repository.DonationRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
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
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@Slf4j
//@AutoConfigureMockMvc
//@AutoConfigureRestDocs
//@SpringBootTest(classes = {PawEverApplication.class})
//@ActiveProfiles("test")
//@ExtendWith({RestDocumentationExtension.class})
//class DonationIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private DonationRepository donationRepository;
//
//    @BeforeEach
//    void setup(WebApplicationContext context , RestDocumentationContextProvider restDocumentation) {
//        this.mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                //.apply(SecurityMockMvcConfigurers.springSecurity()) //secrutiy filter 적용 // Security 설정 완료 후 나중에 적용
//                .apply(documentationConfiguration(restDocumentation))
//                .build();
//    }
//
//    // 불필요한 header 제거 함수
//    private HeadersModifyingOperationPreprocessor getModifiedHeader() {
//        return modifyHeaders().remove("X-Content-Type-Options").remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma").remove("Expires").remove("Content-Length");
//    }
//
//    // response body field - data(Object)
//    private final List<FieldDescriptor> responseFieldDescriptorsForDonation = List.of(
//            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
//            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
//            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
//            fieldWithPath("data").type(JsonFieldType.OBJECT).optional().description("data")
//    );
//
//    // response body field - data(Array)
//    private final List<FieldDescriptor> responseFieldDescriptorsForDonations = List.of(
//            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
//            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
//            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
//            fieldWithPath("data").type(JsonFieldType.ARRAY).description("data"),
//            fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("후원한 사용자 ID"),
//            fieldWithPath("data[].donorName").type(JsonFieldType.STRING).description("기부자 이름"),
//            fieldWithPath("data[].donorMessage").type(JsonFieldType.STRING).description("기부자 메시지"),
//            fieldWithPath("data[].donationAmount").type(JsonFieldType.NUMBER).description("기부 금액"),
//            fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("기부 날짜 (yyyy.MM.dd)")
//    );
//
//    // response body field - error
//    private final List<FieldDescriptor> responseFieldDescriptorsForDonationError = List.of(
//            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
//            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
//            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
//            fieldWithPath("data").type(JsonFieldType.NULL).description("data")
//    );
//
//    private final List<FieldDescriptor> responseFieldDescriptorsForDonationTotalAmount = List.of(
//            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
//            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
//            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
//            fieldWithPath("data.totalAmount").description("전체 후원 금액").type(JsonFieldType.NUMBER).optional()
//    );
//
//    private final List<FieldDescriptor> donationRequestDescriptors = List.of(
//            fieldWithPath("donationAmount").type(JsonFieldType.NUMBER).description("금액"),
//            fieldWithPath("donorName").type(JsonFieldType.STRING).description("후원자명"),
//            fieldWithPath("donorMessage").type(JsonFieldType.STRING).optional().description("후원 메세지").attributes(key("constraint").value("optional")),
//            fieldWithPath("userId").type(JsonFieldType.NUMBER).optional().description("후원한 사용자의 ID")
//    );
//
//    // path parameter field
//    private final List<ParameterDescriptor> pathParametersForDonationByUser = List.of(
//            parameterWithName("user_id").description("후원한 사용자 ID")
//    );
//
//    private ResultActions getResultActionsForCreateDonation(Map<String, Object> request) throws Exception {
//        return mockMvc.perform(
//                RestDocumentationRequestBuilders
//                        .post("/api/donations")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(request))
////                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+token) //jwt 설정 완료 후 도입
//        );
//    }
//
//    private RestDocumentationResultHandler getDocumentForCreateDonation(String identifier){
//        return document("api/donations/createDonations/"+identifier,
//                preprocessRequest(prettyPrint(),modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
//                preprocessResponse(prettyPrint(), getModifiedHeader()),
//                requestFields(donationRequestDescriptors),
//                responseFields(responseFieldDescriptorsForDonation),
////                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer : 사용자 access Token")), //jwt 토큰, security 설정 이후 활성화
//                resource(
//                        ResourceSnippetParameters.builder()
//                                .tag("후원-donation")
//                                .summary("후원 요청 api")
//                                .description("후원을 요청함.")
//                                .build()));
//    }
//
//    @Test
//    public void 후원요청_성공() throws Exception {
//        User testUser = userRepository.save(User.builder()
//                .name("김철수")
//                .email("chulsoo@example.com")
//                .profileImageUrl("https://example.com/profile1.jpg")
//                .socialLoginUuid("uuid-1234")
//                .socialLoginProvider("KAKAO")
//                .role(Role.ROLE_USER)
//                .latitude(new BigDecimal("37.5665"))
//                .longitude(new BigDecimal("126.9780"))
//                .introduction("안녕하세요! 김철수입니다.")
//                .isDeleted(false)
//                .build());
//
//        Long userId = testUser.getUserId();
//
//        Map<String, Object> request = new HashMap<>();
//        request.put("donationAmount", 10000L);
//        request.put("donorName", "김철수");
//        request.put("donorMessage", "좋은 곳에 써주세요!");
//        request.put("userId", userId);
//
//        ResultActions resultActions = getResultActionsForCreateDonation(request);
//        resultActions.andExpect(status().isOk());
//        resultActions.andDo(getDocumentForCreateDonation("success"));
//    }
//
//    @Test
//    public void 후원요청_실패1() throws Exception {
//        User testUser = userRepository.save(User.builder()
//                .name("이영희")
//                .email("younghee@example.com")
//                .profileImageUrl("https://example.com/profile2.jpg")
//                .socialLoginUuid("uuid-5678")
//                .socialLoginProvider("GOOGLE")
//                .role(Role.ROLE_USER)
//                .latitude(new BigDecimal("35.1796"))
//                .longitude(new BigDecimal("129.0756"))
//                .introduction("반갑습니다! 이영희예요.")
//                .isDeleted(false)
//                .build());
//
//        Long userId = testUser.getUserId();
//
//        // 잘못된 금액
//        Map<String, Object> request = new HashMap<>();
//        request.put("donationAmount", -10000L);
//        request.put("donorName", "이영희");
//        request.put("donorMessage", "좋은 곳에 써주세요!");
//        request.put("userId", userId);
//
//        ResultActions resultActions = getResultActionsForCreateDonation(request);
//        resultActions.andExpect(status().isBadRequest()).andExpect(jsonPath("code").value(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode()));
//        resultActions.andDo(getDocumentForCreateDonation("fail"));
//    }
//
//    @Test
//    public void 후원요청_실패2() throws Exception {
//        // 존재하지 않은 사용자 ID
//        Map<String, Object> request = new HashMap<>();
//        request.put("donationAmount", 10000L);
//        request.put("donorName", "김철수");
//        request.put("donorMessage", "좋은 곳에 써주세요!");
//        request.put("userId", 0L);
//        ResultActions resultActions = getResultActionsForCreateDonation(request);
//        resultActions.andExpect(status().isBadRequest()).andExpect(jsonPath("code").value(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode()));
//        resultActions.andDo(getDocumentForCreateDonation("fail"));
//    }
//
//    @Test
//    public void 후원요청_실패3() throws Exception {
//        // 사용자 ID와 후원자 정보 불일치
//        Map<String, Object> request = new HashMap<>();
//        request.put("donationAmount", 10000L);
//        request.put("donorName", "이영희");
//        request.put("donorMessage", "좋은 곳에 써주세요!");
//        request.put("userId", 1000L);
//        ResultActions resultActions = getResultActionsForCreateDonation(request);
//        resultActions.andExpect(status().isBadRequest()).andExpect(jsonPath("code").value(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getCode()));
//        resultActions.andDo(getDocumentForCreateDonation("fail"));
//    }
//
//
//    private ResultActions getResultActionsForGetAllDonations() throws Exception {
//        return mockMvc.perform(
//                RestDocumentationRequestBuilders
//                        .get("/api/users/staff/donations")
//                        .contentType(MediaType.APPLICATION_JSON)
////                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+token) //jwt 설정 완료 후 도입
//        );
//    }
//
//    private RestDocumentationResultHandler getDocumentForGetAllDonations(String identifier){
//        return document("api/donations/getDonations/"+identifier,
//                preprocessRequest(prettyPrint(),modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
//                preprocessResponse(prettyPrint(), getModifiedHeader()),
//                responseFields(responseFieldDescriptorsForDonations),
////                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer : 사용자 access Token")), //jwt 토큰, security 설정 이후 활성화
//                resource(
//                        ResourceSnippetParameters.builder()
//                                .tag("후원-donations")
//                                .summary("전체 후원 내역 조회 api")
//                                .description("후원 내역 전체를 조회함.")
//                                .build()));
//    }
//
//
//    @Test
//    public void 전체_후원내역_조회_성공() throws Exception {
//        User testUser = userRepository.save(User.builder()
//                .name("최지훈")
//                .email("jihoon@example.com")
//                .profileImageUrl("https://example.com/profile4.jpg")
//                .socialLoginUuid("uuid-121314")
//                .socialLoginProvider("GOOGLE")
//                .role(Role.ROLE_USER)
//                .latitude(new BigDecimal("36.3504"))
//                .longitude(new BigDecimal("127.3845"))
//                .introduction("여행을 좋아하는 최지훈입니다.")
//                .isDeleted(false)
//                .build());
//
//        donationRepository.save(Donation.builder()
//                .userId(testUser)
//                .donorName("최지훈")
//                .donorMessage("추가 후원입니다")
//                .donationAmount(5000L)
//                .createdAt(LocalDateTime.now())
//                .build());
//
//        ResultActions resultActions = getResultActionsForGetAllDonations();
//        resultActions.andExpect(status().isOk());
//        resultActions.andDo(getDocumentForGetAllDonations("success"));
//    }
//
//    private ResultActions getResultActionsForGetDonationByUser(Long userId) throws Exception {
//        return mockMvc.perform(
//                RestDocumentationRequestBuilders
//                        .get("/api/users/donations/{user_id}", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
////                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+token) //jwt 설정 완료 후 도입
//        );
//    }
//
//    private RestDocumentationResultHandler getDocumentForGetDonationByUser(String identifier){
//        return document("api/donations/getDonationByUser/"+identifier,
//                preprocessRequest(prettyPrint(),modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
//                preprocessResponse(prettyPrint(), getModifiedHeader()),
//                responseFields(responseFieldDescriptorsForDonations),
//                pathParameters(pathParametersForDonationByUser),
////                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer : 사용자 access Token")), //jwt 토큰, security 설정 이후 활성화
//                resource(
//                        ResourceSnippetParameters.builder()
//                                .tag("후원-donations")
//                                .summary("사용자별 후원 내역 조회 api")
//                                .description("사용자 ID를 통해 사용자별로 후원 내역을 조회함.")
//                                .build()));
//    }
//
//    @Test
//    public void 사용자별_후원내역_조회_성공() throws Exception {
//        ResultActions resultActions = getResultActionsForGetDonationByUser(1L);
//        resultActions.andExpect(status().isOk());
//        resultActions.andDo(getDocumentForGetDonationByUser("success"));
//    }
//
//    @Test
//    public void 사용자별_후원내역_조회_실패() throws Exception {
//        ResultActions resultActions = getResultActionsForGetDonationByUser(30000L);
//        resultActions.andExpect(status().isBadRequest()).andExpect(jsonPath("code").value(ResponseCodeEnum.USER_NOT_FOUND.getCode())).andExpect(jsonPath("data").value(nullValue()));;
//        resultActions.andDo(
//                document("api/donations/getDonationByUser/fail",
//                preprocessRequest(prettyPrint(),modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
//                preprocessResponse(prettyPrint(), getModifiedHeader()),
//                responseFields(responseFieldDescriptorsForDonationError),
//                pathParameters(pathParametersForDonationByUser),
////                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer : 사용자 access Token")), //jwt 토큰, security 설정 이후 활성화
//                resource(
//                        ResourceSnippetParameters.builder()
//                                .tag("후원-donations")
//                                .summary("사용자별 후원 내역 조회 api")
//                                .description("사용자 ID를 통해 사용자별로 후원 내역을 조회함.")
//                                .build())));
//    }
//
//    private ResultActions getResultActionsForGetTotalDonationAmount() throws Exception {
//        return mockMvc.perform(
//                RestDocumentationRequestBuilders
//                        .get("/api/donations/amount")
//                        .contentType(MediaType.APPLICATION_JSON)
////                        .header(HttpHeaders.AUTHORIZATION, "Bearer "+token) //jwt 설정 완료 후 도입
//        );
//    }
//
//    private RestDocumentationResultHandler getDocumentForGetTotalDonationAmount(String identifier){
//        return document("api/donations/getDonationAmount/"+identifier,
//                preprocessRequest(prettyPrint(),modifyUris().scheme("https").host("yellowdog.p-e.kr").removePort()),
//                preprocessResponse(prettyPrint(), getModifiedHeader()),
//                responseFields(responseFieldDescriptorsForDonationTotalAmount),
////                requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer : 사용자 access Token")), //jwt 토큰, security 설정 이후 활성화
//                resource(
//                        ResourceSnippetParameters.builder()
//                                .tag("후원-donations")
//                                .summary("전체 후원 금액 조회 api")
//                                .description("후원 금액 전체를 더한 값을 조회함.")
//                                .build()));
//    }
//
//    @Test
//    public void 전체_후원금액_조회_성공() throws Exception {
//        ResultActions resultActions = getResultActionsForGetTotalDonationAmount();
//        resultActions.andExpect(status().isOk());
//        resultActions.andDo(getDocumentForGetTotalDonationAmount("success"));
//    }
//
//}


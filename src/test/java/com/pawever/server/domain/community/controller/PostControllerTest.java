package com.pawever.server.domain.community.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.PawEverApplication;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.post.controller.PostController;
import com.pawever.server.domain.post.dto.request.PostRequestDTO;
import com.pawever.server.domain.post.dto.response.PostResponseDTO;
import com.pawever.server.domain.post.entity.Post;
import com.pawever.server.domain.post.repository.PostRepository;
import com.pawever.server.domain.post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@SpringBootTest(classes = {PawEverApplication.class})
@ActiveProfiles("test")
@Transactional
@ExtendWith({RestDocumentationExtension.class})
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;


    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext context , RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()) //secrutiy filter 적용 // Security 설정 완료 후 나중에 적용
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    // 블필요한 header 제거 함수
    private HeadersModifyingOperationPreprocessor getModifiedHeader() {
        return modifyHeaders().remove("X-Content-Type-Options").remove("X-XSS-Protection").remove("Cache-Control").remove("Pragma").remove("Expires").remove("Content-Length");
    }

    //response body field 작성
    private final List<FieldDescriptor> responseFieldDescriptorsForPost = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            subsectionWithPath("data").type(JsonFieldType.OBJECT).optional().description("data")
    );

    private final List<FieldDescriptor> responseFieldDescriptorsForPostList = List.of(
            fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
            fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
            fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
            subsectionWithPath("data").type(JsonFieldType.ARRAY).optional().description("data")
    );


    //query parameter  & path parameter field 작성
    private final List<ParameterDescriptor> pathParametersForGetPost = List.of(
            parameterWithName("postId").description("조회할 게시글 ID")
    );


     //게시글 생성 테스트(성공)
    @Test
    void createPostSuccess() throws Exception {
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "request", "", "application/json",
                mapper.writeValueAsBytes(new PostRequestDTO.CreatePostRequest("테스트 제목", "테스트 내용"))
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images", "image1.jpg", "multipart/form-data", "test-image".getBytes()
        );

        mockMvc.perform(
                RestDocumentationRequestBuilders
                        .multipart("/api/community/posts")
                        .file(jsonRequest)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)  //request body content type
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("/api/community/posts/create-post", //API가 저장되는 경로
                        preprocessRequest(prettyPrint(),modifyUris().scheme("http").host("localhost").removePort()),   // request 출력 형식 지정->host 이름 변경
                        preprocessResponse(prettyPrint(), getModifiedHeader()),  // response 출력 형식 지정
                        responseFields(responseFieldDescriptorsForPost),
                        requestParts(
                                partWithName("request").description("게시글 생성 요청 데이터"),
                                partWithName("images").optional().description("첨부 이미지 (선택 사항)")),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community") // 문서에서 api들이 태그로 분류됨 -> 도메인 이름으로 분류하면 좋을 듯.
                                        .summary("커뮤니티 게시글 작성 API") // api 이름
                                        .description("Body는 form-data로 요청하고 Key 값으로는 request와 images 두개로 나눌 수 있습니다." +
                                                "request의 Content-Type는 application/json으로 설정해주시고, Value에 json 형태로 title과 content를 담아주세요" +
                                                "images의 Content-Type는 multipart/form-data로 설정해주시고, Value에는 실제 사진 파일을 담아주세요. 여러장도 가능합니다." +
                                                "사진이 없는 글이라면 images는 완전히 빼고 request json 값만 요청해주세요.") // api 상세 설명
                                        .build())));

    }

    //게시글 생성 테스트(실패)
    @Test
    void createPostFailure() throws Exception {
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "requests", "", "application/json",
                mapper.writeValueAsBytes(new PostRequestDTO.CreatePostRequest("테스트 제목", "테스트 내용"))
        );

        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart("/api/community/posts")
                                .file(jsonRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ResponseCodeEnum.INVALID_REQUEST_ARGUMENT.getStatus().value()))
                .andDo(document("/api/community/posts/create-post-fail",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        responseFields(responseFieldDescriptorsForPost),  // 응답 필드
                        requestParts(
                                partWithName("requests").description("잘못된 요청 필드 (의도적 오류)"),
                                partWithName("images").optional().description("첨부 이미지 (선택 사항)")
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("커뮤니티 게시글 작성 실패 케이스")
                                        .description("이 API는 요청을 잘못 보냈을 경우의 실패 응답을 문서화하기 위한 테스트입니다. " +
                                                "request 필드 대신 requests라는 잘못된 필드를 사용하여 요청을 보냅니다.")
                                        .build())));

    }

    //게시글 단건 조회 (성공)
    @Test
    void getPostSuccess() throws Exception {

        Post post = postRepository.save(Post.builder()
                .userId(1L)
                .title("테스트 제목 1")
                .content("테스트 내용 1")
                .build());

        Long postId = post.getId();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("/api/community/posts/get-post",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(pathParametersForGetPost),  // Path Parameter 문서화
                        responseFields(responseFieldDescriptorsForPost), // Response 필드 문서화
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 단건 조회 API")
                                        .description("`postId`를 이용하여 특정 게시글을 조회합니다.")
                                        .build())));
    }

    //게시글 단건 조회 테스트 (실패 - 게시글 없음)
    @Test
    void getPostFailure_NotFound() throws Exception {
        Long postId = 9999L; // 존재하지 않는 게시글 ID

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ResponseCodeEnum.POST_NOT_FOUND.getStatus().value()))
                .andDo(document("/api/community/posts/get-post-fail",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(pathParametersForGetPost),  // Path Parameter 문서화
                        responseFields(responseFieldDescriptorsForPost), // Response 필드 문서화
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 단건 조회 실패 케이스")
                                        .description("존재하지 않는 `postId`를 조회하여 `POST_NOT_FOUND` 에러를 반환하는 테스트입니다.")
                                        .build())));
    }

    //게시글 전체 조회 (성공)
    @Test
    void getAllPostsSuccess() throws Exception {
        Post post1 = postRepository.save(Post.builder()
                .userId(1L)
                .title("테스트 제목 1")
                .content("테스트 내용 1")
                .build());

        Post post2 = postRepository.save(Post.builder()
                .userId(2L)
                .title("테스트 제목 2")
                .content("테스트 내용 2")
                .build());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/community/posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("/api/community/posts/get-all-post",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        responseFields(responseFieldDescriptorsForPostList), // Response 필드 문서화
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 전체 조회 API")
                                        .description("게시글 목록을 최신순으로 조회하는 API입니다.")
                                        .build())));
    }


    // 게시글 수정 (성공)
    @Test
    void updatePostSuccess() throws Exception {
        // 기존 게시글 저장
        Post post = postRepository.save(Post.builder()
                .userId(1L)
                .title("기존 제목")
                .content("기존 내용")
                .build());

        Long postId = post.getId();

        // 수정 요청 데이터 생성
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "request", "", "application/json",
                mapper.writeValueAsBytes(new PostRequestDTO.UpdatePostRequest("수정된 제목", "수정된 내용"))
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images", "image1.jpg", "multipart/form-data", "test-image".getBytes()
        );

        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart("/api/community/posts/{postId}", postId)
                                .file(jsonRequest)
                                .file(imageFile)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("/api/community/posts/update-post",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(parameterWithName("postId").description("수정할 게시글 ID")),
                        requestParts(
                                partWithName("request").description("게시글 수정 요청 데이터"),
                                partWithName("images").optional().description("첨부 이미지 (선택 사항)")
                        ),
                        responseFields(responseFieldDescriptorsForPost),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 수정 API")
                                        .description("`postId`에 해당하는 게시글을 수정합니다. " +
                                                "Body는 form-data로 요청하며, `request`는 JSON 형식의 게시글 데이터, `images`는 새로운 이미지 파일입니다.")
                                        .build())));
    }

    // 게시글 수정 (실패 - 게시글 없음)
    @Test
    void updatePostFailure_PostNotFound() throws Exception {
        Long postId = 9999L; // 존재하지 않는 게시글 ID

        // 수정 요청 데이터 생성
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "request", "", "application/json",
                mapper.writeValueAsBytes(new PostRequestDTO.UpdatePostRequest("수정된 제목", "수정된 내용"))
        );

        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart("/api/community/posts/{postId}", postId)
                                .file(jsonRequest)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ResponseCodeEnum.POST_NOT_FOUND.getStatus().value()))
                .andDo(document("/api/community/posts/update-post-fail",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(parameterWithName("postId").description("수정할 게시글 ID (존재하지 않는 ID)")),
                        requestParts(partWithName("request").description("게시글 수정 요청 데이터")),
                        responseFields(responseFieldDescriptorsForPost),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 수정 실패 - 게시글 없음")
                                        .description("존재하지 않는 `postId`에 대해 수정 요청을 보냈을 때 `POST_NOT_FOUND` 에러가 반환됩니다.")
                                        .build())));
    }

    // 게시글 수정 (실패 - 권한 없음)
    @Test
    void updatePostFailure_Unauthorized() throws Exception {
        // 기존 게시글 저장 (다른 사용자 ID)
        Post post = postRepository.save(Post.builder()
                .userId(2L) // 현재 테스트에서는 1L이 로그인 유저로 설정됨
                .title("기존 제목")
                .content("기존 내용")
                .build());

        Long postId = post.getId();

        // 수정 요청 데이터 생성
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "request", "", "application/json",
                mapper.writeValueAsBytes(new PostRequestDTO.UpdatePostRequest("수정된 제목", "수정된 내용"))
        );

        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .multipart("/api/community/posts/{postId}", postId)
                                .file(jsonRequest)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ResponseCodeEnum.UNAUTHORIZED_ACTION.getStatus().value()))
                .andDo(document("/api/community/posts/update-post-unauthorized",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(parameterWithName("postId").description("수정할 게시글 ID (권한 없음)")),
                        requestParts(partWithName("request").description("게시글 수정 요청 데이터")),
                        responseFields(responseFieldDescriptorsForPost),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 수정 실패 - 권한 없음")
                                        .description("현재 로그인한 사용자가 게시글의 작성자가 아닐 경우 `UNAUTHORIZED_ACTION` 에러가 반환됩니다.")
                                        .build())));
    }


    // 게시글 삭제 (성공)
    @Test
    void deletePostSuccess() throws Exception {
        // 기존 게시글 저장
        Post post = postRepository.save(Post.builder()
                .userId(1L)
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .build());

        Long postId = post.getId();

        mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/community/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("/api/community/posts/delete-post",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(parameterWithName("postId").description("삭제할 게시글 ID")),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("상태 코드"),
                                subsectionWithPath("data").type(JsonFieldType.OBJECT).optional().description("data (없음)")
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 삭제 API")
                                        .description("해당 `postId`의 게시글을 삭제합니다.")
                                        .build())));
    }

    // 게시글 삭제 (실패 - 게시글 없음)
    @Test
    void deletePostFailure_PostNotFound() throws Exception {
        Long postId = 9999L; // 존재하지 않는 게시글 ID

        mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/community/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ResponseCodeEnum.POST_NOT_FOUND.getStatus().value()))
                .andDo(document("/api/community/posts/delete-post-fail",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(parameterWithName("postId").description("삭제할 게시글 ID (존재하지 않는 ID)")),
                        responseFields(responseFieldDescriptorsForPost),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 삭제 실패 - 게시글 없음")
                                        .description("존재하지 않는 `postId`를 삭제하려고 할 때 `POST_NOT_FOUND` 에러가 반환됩니다.")
                                        .build())));
    }

    // 게시글 삭제 (실패 - 권한 없음)
    @Test
    void deletePostFailure_Unauthorized() throws Exception {
        // 기존 게시글 저장 (다른 사용자 ID)
        Post post = postRepository.save(Post.builder()
                .userId(2L) // 현재 테스트에서는 1L이 로그인 유저로 설정됨
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .build());

        Long postId = post.getId();

        mockMvc.perform(RestDocumentationRequestBuilders
                        .delete("/api/community/posts/{postId}", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(ResponseCodeEnum.UNAUTHORIZED_ACTION.getStatus().value()))
                .andDo(document("/api/community/posts/delete-post-unauthorized",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        pathParameters(parameterWithName("postId").description("삭제할 게시글 ID (권한 없음)")),
                        responseFields(responseFieldDescriptorsForPost),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("게시글 삭제 실패 - 권한 없음")
                                        .description("현재 로그인한 사용자가 게시글의 작성자가 아닐 경우 `UNAUTHORIZED_ACTION` 에러가 반환됩니다.")
                                        .build())));
    }


    // 내가 작성한 게시글 조회 (성공)
    @Test
    void getAllUsersPostSuccess() throws Exception {
        // Given: 로그인된 사용자가 작성한 게시글 저장
        Post post1 = postRepository.save(Post.builder()
                .userId(1L)
                .title("내가 작성한 첫 번째 게시글")
                .content("내용1")
                .build());

        Post post2 = postRepository.save(Post.builder()
                .userId(1L)
                .title("내가 작성한 두 번째 게시글")
                .content("내용2")
                .build());


        // When & Then
        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/community/my-posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("/api/community/posts/get-my-posts",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        responseFields(responseFieldDescriptorsForPostList),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("내가 작성한 게시글 조회 API")
                                        .description("로그인한 사용자가 작성한 모든 게시글을 조회합니다.")
                                        .build())));
    }

    // 내가 작성한 게시글 조회 (실패 - 게시글 없음)
    @Test
    void getAllUsersPostFailure_NoPosts() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/community/my-posts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 성공 응답이지만 데이터는 빈 배열
                .andDo(document("/api/community/posts/get-my-posts-fail",
                        preprocessRequest(prettyPrint(), modifyUris().scheme("http").host("localhost").removePort()),
                        preprocessResponse(prettyPrint(), getModifiedHeader()),
                        responseFields(responseFieldDescriptorsForPostList),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("community")
                                        .summary("내가 작성한 게시글 조회 API (게시글 없음)")
                                        .description("로그인한 사용자가 작성한 게시글이 없을 경우, 빈 배열을 반환합니다.")
                                        .build())));
    }



}

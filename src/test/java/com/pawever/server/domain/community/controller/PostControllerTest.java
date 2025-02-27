package com.pawever.server.domain.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawever.server.domain.post.controller.PostController;
import com.pawever.server.domain.post.dto.request.PostRequestDTO;
import com.pawever.server.domain.post.dto.response.PostResponseDTO;
import com.pawever.server.domain.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;  // API 요청을 모의 실행하기 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환용

    @Test
    void createPost() throws Exception {
        PostRequestDTO.CreatePostRequest requestDTO = new PostRequestDTO.CreatePostRequest("이미지 포함", "내용1");
        String requestJson = objectMapper.writeValueAsString(requestDTO);

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        mockMvc.perform(multipart("/api/community/post")
                        .file(image)          // 이미지 파일 추가
                        .file(requestPart)    // JSON 요청 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andDo(document("community/post/create"));
    }

}
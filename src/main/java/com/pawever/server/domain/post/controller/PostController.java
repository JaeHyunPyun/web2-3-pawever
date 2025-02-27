package com.pawever.server.domain.post.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.post.dto.request.PostRequestDTO;
import com.pawever.server.domain.post.dto.response.PostResponseDTO;
import com.pawever.server.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community")
public class PostController {
    private final PostService postService;

    //게시글 작성
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse> createPost(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart PostRequestDTO.CreatePostRequest request) {

        //멤버 아이디 임의 설정
        Long userId = 1L;

        // 게시글 생성
        PostResponseDTO.PostResponse response = postService.createPost(request, userId, images);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCodeEnum.CREATED, response));
    }

    //게시글 단건 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse> getPost(
            @PathVariable Long postId) {

        // 게시글 조회
        PostResponseDTO.PostResponse response = postService.getPost(postId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }


    //게시글 전체 조회
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse> getAllPost() {
        // 서비스 호출하여 게시글 전체 조회
        List<PostResponseDTO.PostResponse> response = postService.getAllPosts();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

    //게시글 수정
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse> updatePost(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable Long postId,
            @RequestPart PostRequestDTO.UpdatePostRequest request) {

        //멤버 아이디 임의 설정
        Long userId = 1L;

        PostResponseDTO.PostResponse response = postService.updatePost(postId, userId, request, images);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }


    //게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId) {
        //멤버 아이디 임의 설정
        Long userId = 1L;

        // 게시글 삭제
        postService.deletePost(postId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }


    //로그인 한 회원이 작성한 게시글 전체 조회
    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse> getAllUsersPost() {
        //멤버 아이디 임의 설정
        Long userId = 1L;

        List<PostResponseDTO.PostResponse> response = postService.getAllUsersPosts(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

}
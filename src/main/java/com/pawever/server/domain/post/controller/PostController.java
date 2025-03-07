package com.pawever.server.domain.post.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.response.CareHubResponseDTO;
import com.pawever.server.domain.post.dto.request.PostRequestDTO;
import com.pawever.server.domain.post.dto.response.PostResponseDTO;
import com.pawever.server.domain.post.service.PostService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import com.pawever.server.domain.user.service.RefreshTokenService;
import com.pawever.server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/community")
@Tag(name = "게시글 API")
public class PostController {
    private final PostService postService;
    private final AccessTokenService accessTokenService;
    private final JwtUtil jwtUtil;

    //게시글 작성
    @PostMapping("/posts")
    @Operation(summary = "게시글 작성")
    public ResponseEntity<ApiResponse> createPost(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "request", required = true) PostRequestDTO.CreatePostRequest request,
            HttpServletRequest httpServletRequest) {

        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        // 게시글 생성
        PostResponseDTO.PostResponse response = postService.createPost(request, userId, images);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCodeEnum.CREATED, response));
    }

    //게시글 단건 조회
    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 단건 조회")
    public ResponseEntity<ApiResponse> getPost(
            @PathVariable Long postId) {

        // 게시글 조회
        PostResponseDTO.PostResponse response = postService.getPost(postId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }


    //게시글 전체 조회
    @Operation(summary = "게시글 전체 조회")
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse> getAllPost() {
        // 서비스 호출하여 게시글 전체 조회
        List<PostResponseDTO.PostResponse> response = postService.getAllPosts();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

    //게시글 수정
    @PatchMapping("/posts/{postId}")
    @Operation(summary = "게시글 수정")
    public ResponseEntity<ApiResponse> updatePost(
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable Long postId,
            @RequestPart PostRequestDTO.UpdatePostRequest request,
            HttpServletRequest httpServletRequest) {

        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        PostResponseDTO.PostResponse response = postService.updatePost(postId, userId, request, images);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }


    //게시글 삭제
    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long postId, HttpServletRequest httpServletRequest) {

        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        // 게시글 삭제
        postService.deletePost(postId, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }


    //로그인 한 회원이 작성한 게시글 전체 조회
    @GetMapping("/my-posts")
    @Operation(summary = "내가 작성한 게시글 조회")
    public ResponseEntity<ApiResponse> getAllUsersPost(HttpServletRequest httpServletRequest) {

        String accessToken = accessTokenService.getRequestAccessToken(httpServletRequest);
        Long userId = jwtUtil.getUserId(accessToken);

        List<PostResponseDTO.PostResponse> response = postService.getAllUsersPosts(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCodeEnum.SUCCESS, response));
    }

    //게시판 검색 기능 구현
    @GetMapping("/posts/search")
    @Operation(summary = "제목으로 게시글 검색")
    public ResponseEntity<ApiResponse> getSearchPosts(
            @RequestParam(name = "q", required = false) String q
    ) {
        List<PostResponseDTO.PostResponse> searchedPosts = postService.searchPosts(q);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, searchedPosts));
    }

}
package com.pawever.server.domain.community.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.community.dto.ReplyListResponseDto;
import com.pawever.server.domain.community.dto.ReplyRequestDto;
import com.pawever.server.domain.community.dto.ReplyResponseDto;
import com.pawever.server.domain.community.service.ReplyService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 게시글 댓글 API")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping("/{postId}/replies")
    @Operation(summary = "댓글 작성 API")
    public ResponseEntity<ApiResponse> createReply(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ReplyRequestDto requestDto) {

        String userUuid = customUserDetails.getUsername();
        ReplyResponseDto responseDto = replyService.createReply(postId, userUuid, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCodeEnum.CREATED, responseDto));
    }

    @GetMapping("/{postId}/replies")
    @Operation(summary = "댓글 목록 조회 API")
    public ResponseEntity<ApiResponse> getAllReplies(
            @PathVariable Long postId) {

        ReplyListResponseDto responseDto = replyService.getAllRepliesByPostId(postId);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, responseDto));
    }

    @PutMapping("/{postId}/replies/{replyId}")
    @Operation(summary = "댓글 수정 API")
    public ResponseEntity<ApiResponse> updateReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ReplyRequestDto requestDto) {

        String userUuid = customUserDetails.getUsername();
        ReplyResponseDto responseDto = replyService.updateReply(postId, replyId, userUuid, requestDto);


        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, responseDto));
    }

    @DeleteMapping("/{postId}/replies/{replyId}")
    @Operation(summary = "댓글 삭제 API")
    public ResponseEntity<ApiResponse> deleteReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String userUuid = customUserDetails.getUsername();
        replyService.deleteReply(postId, replyId, userUuid);

        return ResponseEntity.noContent().build();
    }
}

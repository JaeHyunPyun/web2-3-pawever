package com.pawever.server.domain.community.controller;

import com.pawever.server.domain.community.dto.ReplyListResponseDto;
import com.pawever.server.domain.community.dto.ReplyRequestDto;
import com.pawever.server.domain.community.dto.ReplyResponseDto;
import com.pawever.server.domain.community.dto.ReplyScrollResponseDto;
import com.pawever.server.domain.community.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    // 댓글 작성
    @PostMapping("/{postId}/replies")
    public ResponseEntity<ReplyResponseDto> createReply(
            @PathVariable Long postId,
            @Valid @RequestBody ReplyRequestDto requestDto) {

        ReplyResponseDto responseDto = replyService.createReply(postId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 게시글의 모든 댓글 목록 조회
    @GetMapping("/{postId}/replies")
    public ResponseEntity<ReplyListResponseDto> getAllReplies(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "false") boolean oldestFirst) {

        ReplyListResponseDto responseDto = replyService.getAllRepliesByPostId(postId);
        return ResponseEntity.ok(responseDto);
    }


    // 댓글 수정
    @PutMapping("/{postId}/replies/{replyId}")
    public ResponseEntity<ReplyResponseDto> updateReply(
            @PathVariable Long postId,
            @PathVariable Long replyId,
            @Valid @RequestBody ReplyRequestDto requestDto) {

        ReplyResponseDto responseDto = replyService.updateReply(postId, replyId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long postId,
            @PathVariable Long replyId) {

        replyService.deleteReply(postId, replyId);
        return ResponseEntity.noContent().build();
    }
}
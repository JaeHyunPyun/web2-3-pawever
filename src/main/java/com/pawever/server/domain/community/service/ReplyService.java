package com.pawever.server.domain.community.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.community.dto.ReplyListResponseDto;
import com.pawever.server.domain.community.dto.ReplyRequestDto;
import com.pawever.server.domain.community.dto.ReplyResponseDto;
import com.pawever.server.domain.community.entity.Reply;
import com.pawever.server.domain.community.repository.ReplyRepository;
import com.pawever.server.domain.post.entity.Post;
import com.pawever.server.domain.post.repository.PostRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private static final Long TEST_USER_ID = 22L; // userId를 10으로 고정

    // 댓글 생성
    @Transactional
    public ReplyResponseDto createReply(Long postId, ReplyRequestDto requestDto) {
        User user = userRepository.findById(TEST_USER_ID)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.POST_NOT_FOUND));

        Reply reply = Reply.builder()
                .user(user)
                .post(post)
                .content(requestDto.getContent())
                .build();

        return new ReplyResponseDto(replyRepository.save(reply));
    }

    // 특정 게시글의 모든 댓글 목록 조회
    @Transactional(readOnly = true)
    public ReplyListResponseDto getAllRepliesByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ResponseCodeEnum.POST_NOT_FOUND);
        }

        List<Reply> replies = replyRepository.findByPostIdOrderByCreatedAtAsc(postId);
        List<ReplyResponseDto> repliesDto = replies.stream()
                .map(ReplyResponseDto::new)
                .collect(Collectors.toList());

        return new ReplyListResponseDto(repliesDto);
    }

    // 댓글 수정
    @Transactional
    public ReplyResponseDto updateReply(Long postId, Long replyId, ReplyRequestDto requestDto) {
        Reply reply = findReplyByPostIdAndReplyId(postId, replyId);

        // userId = 10이 작성자인지 확인
        if (!reply.getUser().getUserId().equals(TEST_USER_ID)) {
            throw new CustomException(ResponseCodeEnum.REPLY_ACCESS_DENIED);
        }

        reply.updateContent(requestDto.getContent());
        return new ReplyResponseDto(reply);
    }

    // 댓글 삭제
    @Transactional
    public void deleteReply(Long postId, Long replyId) {
        Reply reply = findReplyByPostIdAndReplyId(postId, replyId);

        // userId = 10이 작성자인지 확인
        if (!reply.getUser().getUserId().equals(TEST_USER_ID)) {
            throw new CustomException(ResponseCodeEnum.REPLY_ACCESS_DENIED);
        }

        replyRepository.delete(reply);
    }

    // 게시글과 댓글 ID로 댓글 찾기 (중복 코드 제거)
    private Reply findReplyByPostIdAndReplyId(Long postId, Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.REPLY_NOT_FOUND));

        if (!reply.getPost().getId().equals(postId)) {
            throw new CustomException(ResponseCodeEnum.REPLY_POST_MISMATCH);
        }

        return reply;
    }
}

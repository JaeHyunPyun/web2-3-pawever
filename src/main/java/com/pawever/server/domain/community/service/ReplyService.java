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

    @Transactional
    public ReplyResponseDto createReply(Long postId, Long userId,ReplyRequestDto requestDto) {
        User user = userRepository.findById(userId)
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

    @Transactional(readOnly = true)
    public ReplyListResponseDto getAllRepliesByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ResponseCodeEnum.POST_NOT_FOUND);
        }

        List<Reply> replies = replyRepository.findByPostIdOrderByCreatedAtDesc(postId);
        List<ReplyResponseDto> repliesDto = replies.stream()
                .map(ReplyResponseDto::new)
                .collect(Collectors.toList());

        return new ReplyListResponseDto(repliesDto);
    }

    @Transactional
    public ReplyResponseDto updateReply(Long postId, Long replyId, Long userId, ReplyRequestDto requestDto) {
        Reply reply = findReplyByPostIdAndReplyId(postId, replyId);
        reply.updateContent(requestDto.getContent(), userId);
        return new ReplyResponseDto(reply);
    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, Long userId) {
        Reply reply = findReplyByPostIdAndReplyId(postId, replyId);
        reply.validateOwner(userId);
        replyRepository.delete(reply);
    }


    // 게시글과 댓글 ID로 댓글 찾기
    private Reply findReplyByPostIdAndReplyId(Long postId, Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.REPLY_NOT_FOUND));

        if (!reply.getPost().getId().equals(postId)) {
            throw new CustomException(ResponseCodeEnum.REPLY_POST_MISMATCH);
        }

        return reply;
    }
}

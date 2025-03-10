package com.pawever.server.domain.community.repository;

import com.pawever.server.domain.community.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @Query("SELECT r FROM Reply r JOIN FETCH r.user WHERE r.post.id = :postId ORDER BY r.createdAt DESC")
    List<Reply> findByPostIdOrderByCreatedAtDesc(Long postId);
}
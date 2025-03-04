package com.pawever.server.domain.community.repository;

import com.pawever.server.domain.community.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostIdOrderByCreatedAtAsc(Long postId);
}
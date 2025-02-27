package com.pawever.server.domain.post.repository;

import com.pawever.server.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    //로그인 한 회원이 작성한 모든 게시글 조회
    @Query("SELECT p FROM Post p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    List<Post> findAllByUserId(@Param("userId") Long userId);
}
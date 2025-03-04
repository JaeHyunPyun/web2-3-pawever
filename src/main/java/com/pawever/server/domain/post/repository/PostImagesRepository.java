package com.pawever.server.domain.post.repository;

import com.pawever.server.domain.post.entity.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, Long> {
    // 특정 게시글(article_id)의 모든 이미지 URL 조회
    @Query("SELECT pi.imageUrl FROM PostImages pi WHERE pi.post.id = :postId")
    List<String> findImageUrlsByPostId(@Param("postId") Long postId);

    // 게시글 전체 조회시 최적화
    @Query("SELECT pi.post.id, pi.imageUrl FROM PostImages pi WHERE pi.post.id IN :postIds")
    List<Object[]> findImageUrlsByPostIds(@Param("postIds") List<Long> postIds);

    @Transactional
    void deleteByPostId(Long postId);

}

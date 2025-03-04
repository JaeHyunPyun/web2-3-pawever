package com.pawever.server.domain.post.entity;

import com.pawever.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "post")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //멤버 구현 전 임시로 매핑
    @Column(name = "user_id", nullable = false)
    private Long userId = 1L;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id", nullable = false)
//    private Member member;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;



    //제목 변경 메서드
    public void setTitle(String title) {
        this.title = title;
    }

    //내용 변경 메서드
    public void setContent(String content) {
        this.content = content;
    }
}
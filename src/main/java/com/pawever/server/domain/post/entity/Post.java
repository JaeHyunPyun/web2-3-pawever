package com.pawever.server.domain.post.entity;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.user.entity.jpa.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reply> replies;





    //제목 변경 메서드
    public void setTitle(String title) {
        this.title = title;
    }

    //내용 변경 메서드
    public void setContent(String content) {
        this.content = content;
    }

    //썸네일 변경 메서드
    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
}

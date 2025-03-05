package com.pawever.server.domain.LikedPet.entity;

import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.user.entity.jpa.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "LIKED_PET")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikedPet {

    @EmbeddedId
    private LikedPetId id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "abandoned_pet_id", insertable = false, updatable = false)
    private Long abandonedPetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public LikedPet(Long userId, Long abandonedPetId) {
        this.id = new LikedPetId(userId, abandonedPetId);
    }
}
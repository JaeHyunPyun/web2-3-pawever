package com.pawever.server.domain.user.entity.jpa;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.user.converter.BooleanToYNConverter;
import com.pawever.server.domain.user.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;  // 유저 id

    @Column(name= "name", nullable = false, length=50)
    private String name; // 유저 name

    @Column(name= "email", nullable = false, length=255)
    private String email; // 유저 email

    @Column(name= "profile_image_url", length=255)
    private String profileImageUrl; // 유저 profile image

    @Column(name= "social_login_uuid", nullable = false, unique = true, length=36)
    private String socialLoginUuid; // 카카오, 구글 제공 uuid

    @Column(name = "social_login_provider", nullable = false, length=10)
    private String socialLoginProvider; // 카카오, 구글

    @Setter
    @Column(name = "latitude", nullable = true, precision = 10, scale = 7)
    private BigDecimal latitude;  // 유저 회원가입시 위치(위도)

    @Setter
    @Column(name = "longitude", nullable = true, precision = 10, scale = 7)
    private BigDecimal longitude;  // 유저 회원가입시 위치(경도)

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.ROLE_USER; // 유저 권한

    @Column(name="introduction", length=255)
    private String introduction;        // 유저 자기소개

    @Column(name="deleted_at")
    private LocalDateTime deleted_at; // 삭제시각(수동 업데이트)

    @Column(name="is_deleted", nullable = false, length = 10)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isDeleted; // 삭제 여부, 기본값 false(N)

    public void updateUserProfile(String name, String introduction){
        if(name != null){
            this.name = name;
        }

        if(introduction != null){
            this.introduction = introduction;
        }
    }

    public void updateProfileImageUrl(String profileImageUrl){
        if(profileImageUrl != null){
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updateUserRole(Role role){
        this.role =role;
    }

}
package com.pawever.server.domain.carehub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "district_code")
public class DistrictCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_cd", nullable = false, unique = true)
    private String orgCd;  // 시군구 코드

    @Column(name = "upr_cd", nullable = false)
    private String uprCd; // 상위 시도 코드

    @Column(name = "name", nullable = false)
    private String name;  // 시군구 이름
}

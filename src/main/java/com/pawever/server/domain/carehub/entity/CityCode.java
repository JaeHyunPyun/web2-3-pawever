package com.pawever.server.domain.carehub.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "city_code")
public class CityCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK

    @Column(name = "org_cd", nullable = false, unique = true, length = 10)
    private String orgCd;  // 시도 코드 (API의 orgCd)

    @Column(name = "name", nullable = false, length = 50)
    private String name;  // 시도 이름 (API의 orgdownNm)
}

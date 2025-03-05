package com.pawever.server.domain.carehub.entity;

import com.pawever.server.domain.carehub.dto.response.AbandonedPetApiResponse;
import com.pawever.server.domain.carehub.enums.NeuteredStatus;
import com.pawever.server.domain.carehub.enums.Sex;
import com.pawever.server.domain.carehub.enums.Species;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "abandoned_pet")
public class AbandonedPet {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;  // desertion_no (API 제공 고유 유기 번호)

    @Column(name = "provider_shelter_id", nullable = false)
    private Long providerShelterId;  // API에서 제공하는 보호소 고유 ID

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "name", nullable = false, length = 255)
    private String name;  // 이름 (품종 + 연생 + 성별 조합)

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false, length = 10)
    private Species species;  // 축종 (DOG 또는 CAT)

    @Column(name = "breed", nullable = false, length = 255)
    private String breed;  // 품종

    @Enumerated(EnumType.STRING)
    @Column(name = "neutered_status", nullable = false, length = 10)
    private NeuteredStatus neuteredStatus;  // 중성화 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false, length = 10)
    private Sex sex;  // 성별

    @Column(name = "age", nullable = false, length = 50)
    private String age;  // 연생

    @Column(name = "found_location", nullable = false, length = 255)
    private String foundLocation;  // 발견장소

    @Column(name = "weight", nullable = false, length = 50)
    private String weight;  // 체중

    @Column(name = "color", nullable = false, length = 255)
    private String color;  // 색

    @Column(name = "characteristics", length = 255)
    private String characteristics;  // 특징 (nullable)

    @Column(name = "notice_number", nullable = false, length = 255)
    private String noticeNumber;  // 공고번호

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "district_code")
    private String districtCode;
}
package com.pawever.server.domain.carehub.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!test")
@Service
@RequiredArgsConstructor
public class InitializationService {

    private final CityCodeService cityCodeService;
    private final DistrictCodeService districtCodeService;
    private final ShelterService shelterService;
    private final AbandonedPetService abandonedPetService;

    @Value("${openapi.service-key}")
    private String serviceKey;

    @PostConstruct  // 서버 시작 시 자동 실행
    public void initializeData() {
        cityCodeService.fetchAndSaveCityCodes(serviceKey);      // 시도 코드 저장
        districtCodeService.fetchAndSaveDistrictCodes(serviceKey); // 시군구 코드 저장
        shelterService.fetchAndSaveShelters(serviceKey);       // 보호소 저장
        abandonedPetService.fetchAndSaveAbandonedPets(serviceKey); // 유기동물 정보 저장

    }
}

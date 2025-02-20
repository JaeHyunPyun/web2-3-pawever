package com.pawever.server.domain.carehub.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitializationService {

    private final CityCodeService cityCodeService;
    private final DistrictCodeService districtCodeService;
    private final ShelterService shelterService;

    @Value("${openapi.service-key}")
    private String serviceKey;

    @PostConstruct  // 서버 시작 시 자동 실행
    public void initializeData() {
        cityCodeService.fetchAndSaveCityCodes(serviceKey);      // 시도 코드 저장
        districtCodeService.fetchAndSaveDistrictCodes(serviceKey); // 시군구 코드 저장
        shelterService.fetchAndSaveShelters(serviceKey);       // 보호소 저장
    }
}

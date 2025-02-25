package com.pawever.server.domain.carehub.service;

import com.pawever.server.domain.carehub.dto.response.ShelterApiResponse;
import com.pawever.server.domain.carehub.entity.CityCode;
import com.pawever.server.domain.carehub.entity.DistrictCode;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.repository.DistrictCodeRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final DistrictCodeRepository districtCodeRepository;
    private final WebClient webClient = WebClient.builder().build();

    public void fetchAndSaveShelters(String serviceKey) {
        if (shelterRepository.count() > 0) {
            log.info("보호소 데이터가 이미 데이터베이스에 존재 => API 호출 생략");
            return;
        }

        List<DistrictCode> districtCodes = districtCodeRepository.findAll();

        districtCodes.forEach(districtCode -> {
            String url = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicSrvc/shelter")
                    .queryParam("upr_cd", districtCode.getUprCd())
                    .queryParam("org_cd", districtCode.getOrgCd())
                    .queryParam("_type", "json")
                    .queryParam("serviceKey", serviceKey)
                    .build(false)
                    .toUriString();

            log.info("보호소 API 호출 - URL: {}", url);


            try {
                ShelterApiResponse response = webClient.get()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(ShelterApiResponse.class)
                        .block();

                if (response != null && response.getResponse() != null && response.getResponse().getBody() != null) {
                    ShelterApiResponse.Items items = response.getResponse().getBody().getItems();

                    if (items != null && items.getItem() != null && !items.getItem().isEmpty()) {
                        items.getItem().forEach(item -> {
                            Shelter shelter = Shelter.builder()
                                    .providerShelterId(Long.valueOf(item.getCareRegNo()))
                                    .name(item.getCareNm())
                                    .userId(null)
                                    .centerPhoneNumber(null)
                                    .managerPhoneNumber(null)
                                    .cityCode(districtCode.getUprCd())
                                    .districtCode(districtCode.getOrgCd())
                                    .eupmyeondong(null)
                                    .roadAddress(null)
                                    .latitude(BigDecimal.ZERO)
                                    .longitude(BigDecimal.ZERO)
                                    .build();

                            shelterRepository.save(shelter);
                            log.info("보호소 저장 완료 - 이름: {}, 고유ID: {}", item.getCareNm(), item.getCareRegNo());
                        });
                    }

                } else {
                    log.warn("보호소 API 호출 실패 또는 데이터 없음 - 시도 코드: {}, 시군구 코드: {}", districtCode.getUprCd(), districtCode.getOrgCd());
                }
            } catch (Exception e) {
                log.error("시군구 코드 조회 실패 (시도 코드: {}, 시군구 코드: {}): {}", districtCode.getUprCd(), districtCode.getOrgCd(), e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }
}

package com.pawever.server.domain.carehub.service;

import com.pawever.server.domain.carehub.dto.response.DistrictCodeApiResponse;
import com.pawever.server.domain.carehub.entity.CityCode;
import com.pawever.server.domain.carehub.entity.DistrictCode;
import com.pawever.server.domain.carehub.repository.CityCodeRepository;
import com.pawever.server.domain.carehub.repository.DistrictCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistrictCodeService {
    private final CityCodeRepository cityCodeRepository;
    private final DistrictCodeRepository districtCodeRepository;
    private final WebClient webClient = WebClient.builder().build();


    public void fetchAndSaveDistrictCodes(String serviceKey) {
        if (districtCodeRepository.count() > 0) {
            log.info("시군구 코드가 이미 데이터베이스에 존재 => API 호출 생략");
            return;
        }


        List<CityCode> cityCodes = cityCodeRepository.findAll();

        cityCodes.forEach(cityCode -> {
            String url = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicSrvc/sigungu")
                    .queryParam("_type", "json")
                    .queryParam("upr_cd", cityCode.getOrgCd())
                    .queryParam("serviceKey", serviceKey)
                    .build(false)
                    .toUriString();

            log.info("시군구 코드 API 호출 (시도 코드: {}) - URL: {}", cityCode.getOrgCd(), url);

            try {
                DistrictCodeApiResponse response = webClient.get()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(DistrictCodeApiResponse.class)
                        .block();

                if (response != null && response.getResponse() != null && response.getResponse().getBody() != null) {
                    response.getResponse().getBody().getItems().getItem().forEach(item -> {
                        if (!districtCodeRepository.existsByOrgCd(item.getOrgCd())) {
                            districtCodeRepository.save(DistrictCode.builder()
                                    .orgCd(item.getOrgCd())
                                    .uprCd(item.getUprCd())
                                    .name(item.getOrgdownNm())
                                    .build());
                            log.info("시군구 코드 저장 - {} ({})", item.getOrgdownNm(), item.getOrgCd());
                        } else {
                            log.warn("이미 존재하는 시군구 코드 - {} ({})", item.getOrgdownNm(), item.getOrgCd());
                        }
                    });
                } else {
                    log.warn("시군구 코드 데이터 없음 (시도 코드: {})", cityCode.getOrgCd());
                }

            } catch (Exception e) {
                log.error("시군구 코드 조회 실패 (시도 코드: {}): {}", cityCode.getOrgCd(), e.getMessage(), e);
            }
        });
    }
}
package com.pawever.server.domain.carehub.service;

import com.pawever.server.domain.carehub.dto.response.CityCodeApiResponse;
import com.pawever.server.domain.carehub.entity.CityCode;
import com.pawever.server.domain.carehub.repository.CityCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityCodeService {

    private final CityCodeRepository cityCodeRepository;
    private final WebClient webClient = WebClient.builder().build();

    public void fetchAndSaveCityCodes(String serviceKey) {
        if (cityCodeRepository.count() > 0) {
            log.info("시도 코드가 이미 데이터베이스에 존재 => API 호출 생략");
            return;
        }  // 시도 코드가 존재하면 중단

        String url = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("apis.data.go.kr")
                .path("/1543061/abandonmentPublicSrvc/sido")
                .queryParam("serviceKey", serviceKey)
                .queryParam("_type", "json")
                .build(false)
                .toUriString();

        log.info("시도 코드 API 호출 - URL: {}", url);

        CityCodeApiResponse response = webClient.get()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CityCodeApiResponse.class)
                .block();


        if (response != null && response.getResponse() != null && response.getResponse().getBody() != null) {
            response.getResponse().getBody().getItems().getItem().forEach(item -> cityCodeRepository.save(
                    CityCode.builder()
                            .orgCd(item.getOrgCd())
                            .name(item.getOrgdownNm())
                            .build()
            ));
        }
    }
}
package com.pawever.server.domain.carehub.service;

import com.pawever.server.domain.carehub.dto.response.CityCodeApiResponse;
import com.pawever.server.domain.carehub.entity.CityCode;
import com.pawever.server.domain.carehub.repository.CityCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CityCodeService {

    private final CityCodeRepository cityCodeRepository;
    private final WebClient webClient = WebClient.builder().build();

    public void fetchAndSaveCityCodes(String serviceKey) {
        if (cityCodeRepository.count() > 0) return;  // 시도 코드가 존재하면 중단

        String url = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("apis.data.go.kr")
                .path("/1543061/abandonmentPublicSrvc/sido")
                .queryParam("serviceKey", serviceKey)
                .queryParam("_type", "json")
                .build()
                .toUriString();

        CityCodeApiResponse response = webClient.get()
                .uri(url)
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
package com.pawever.server.domain.carehub.service;


import com.pawever.server.domain.carehub.dto.response.AbandonedPetApiResponse;
import com.pawever.server.domain.carehub.entity.AbandonedPetTemp;
import com.pawever.server.domain.carehub.entity.DistrictCode;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.enums.NeuteredStatus;
import com.pawever.server.domain.carehub.enums.Sex;
import com.pawever.server.domain.carehub.enums.Species;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import com.pawever.server.domain.carehub.repository.AbandonedPetTempRepository;
import com.pawever.server.domain.carehub.repository.DistrictCodeRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbandonedPetService {
    private final AbandonedPetTempRepository abandonedPetTempRepository;
    private final AbandonedPetRepository abandonedPetRepository;
    private final ShelterRepository shelterRepository;
    private final DistrictCodeRepository districtCodeRepository;
    private final WebClient webClient = WebClient.builder().build();
    private final JdbcTemplate jdbcTemplate;

    private static final int MAX_CONCURRENT_REQUESTS = 20;
    private static final int NUM_OF_ROWS = 1000; //한 페이지에 보여줄 응답 수 (페이지 네이션 제거)

    public void fetchAndSaveAbandonedPets(String serviceKey) {
        if (abandonedPetRepository.count() == 0) {
            swapTableNames();
        }
        if (abandonedPetTempRepository.count() > 0) {
            log.info("유기동물 데이터가 이미 데이터베이스에 존재 => API 호출 생략");
            return;
        }
        List<DistrictCode> districtCodes = districtCodeRepository.findAll();
        Semaphore rateLimiter = new Semaphore(MAX_CONCURRENT_REQUESTS);

        Flux.fromIterable(districtCodes)
                .flatMapSequential(districtCode -> Mono.defer(() -> {
                    try {
                        rateLimiter.acquire(); // 동시성 제어
                        return fetchAbandonedPetData(serviceKey, districtCode)
                                .flatMap(response -> Mono.fromRunnable(() -> handleApiResponse(response, districtCode)))
                                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                                .doFinally(signal -> rateLimiter.release());
                    } catch (InterruptedException e) {
                        log.error("Semaphore 획득 실패 - 시도 코드: {}, 시군구 코드: {}", districtCode.getUprCd(), districtCode.getOrgCd());
                        return Mono.empty();
                    }
                }))
                .blockLast();
    }

    private Mono<AbandonedPetApiResponse> fetchAbandonedPetData(String serviceKey, DistrictCode districtCode) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("apis.data.go.kr")
                .path("/1543061/abandonmentPublicSrvc/abandonmentPublic")
                .queryParam("numOfRows", NUM_OF_ROWS)
                .queryParam("serviceKey", serviceKey)
                .queryParam("_type", "json")
                .queryParam("state", "protect")
                .queryParam("upr_cd", districtCode.getUprCd())
                .queryParam("org_cd", districtCode.getOrgCd())
                .build(false)
                .toUriString();

        return webClient.get()
                .uri(URI.create(url))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AbandonedPetApiResponse.class);
    }

    public void handleApiResponse(AbandonedPetApiResponse response, DistrictCode districtCode) {
        if (response == null || response.getResponse() == null || response.getResponse().getBody() == null) {
            log.warn("API 응답 없음 - 시도 코드: {}, 시군구 코드: {}", districtCode.getUprCd(), districtCode.getOrgCd());
            return;
        }

        var items = response.getResponse().getBody().getItems().getItem();
        if (items == null || items.isEmpty()) {
            log.warn("API 응답 바디 없음 - 시도 코드: {}, 시군구 코드: {}", districtCode.getUprCd(), districtCode.getOrgCd());
            return;
        }

        log.info("API 응답 데이터 개수 - 시도 코드: {}, 시군구 코드: {}, 개수: {}", districtCode.getUprCd(), districtCode.getOrgCd(), items.size());

        items.forEach(item -> {
            try {
                saveAbandonedPet(item, districtCode.getUprCd(), districtCode.getOrgCd());
                updateShelter(item, districtCode.getUprCd(), districtCode.getOrgCd());
            } catch (Exception e) {
                log.error("데이터 저장 실패 - ID: {}, 이유: {}", item.getDesertionNo(), e.getMessage());
            }
        });
    }




    public void saveAbandonedPet(AbandonedPetApiResponse.Item item, String uprCd, String orgCd) {
        Shelter shelter = shelterRepository.findByNameAndCityCodeAndDistrictCode(item.getCareNm().trim(), uprCd, orgCd)
                .orElse(null);

        if (shelter == null) {
            log.warn("매핑된 보호소를 찾을 수 없음 - 보호소명: {}, 시도 코드: {}, 시군구 코드: {}", item.getCareNm(), uprCd, orgCd);
            return;
        }

        if (abandonedPetTempRepository.existsById(Long.valueOf(item.getDesertionNo()))) {
            log.info("중복된 유기동물 발견 - ID: {}", item.getDesertionNo());
            return;
        }

        AbandonedPetTemp abandonedPetTemp = AbandonedPetTemp.builder()
                .id(Long.valueOf(item.getDesertionNo()))
                .providerShelterId(shelter.getProviderShelterId())
                .imageUrl(item.getFilename())
                .name(buildPetName(item))
                .species(resolveSpecies(item.getKindCd()))
                .breed(resolveBreed(item.getKindCd()))
                .neuteredStatus(resolveNeuteredStatus(item.getNeuterYn()))
                .sex(resolveSex(item.getSexCd()))
                .age(item.getAge())
                .foundLocation(item.getHappenPlace())
                .weight(item.getWeight())
                .color(item.getColorCd())
                .characteristics(item.getSpecialMark())
                .noticeNumber(item.getNoticeNo())
                .build();

        try {
            abandonedPetTempRepository.saveAndFlush(abandonedPetTemp); // 즉시 반영
            log.info("유기동물 저장 완료 - ID: {}", item.getDesertionNo());
        } catch (Exception e) {
            log.error("유기동물 저장 실패 - ID: {}, 이유: {}", item.getDesertionNo(), e.getMessage());
        }
    }

    public void updateShelter(AbandonedPetApiResponse.Item item, String uprCd, String orgCd) {
        shelterRepository.findByNameAndCityCodeAndDistrictCode(item.getCareNm(), uprCd, orgCd).ifPresent(shelter -> {
            if (shelter.getCenterPhoneNumber() == null) {
                String[] addressParts = item.getCareAddr().trim().split(" ");

                shelter.updateInfo(
                        item.getOfficetel(),
                        item.getCareTel(),
                        addressParts.length > 0 ? addressParts[0] : null, // sido
                        addressParts.length > 1 ? addressParts[1] : null, // sigungu
                        addressParts.length > 2 ? addressParts[2] : null, // eupmyeondong
                        addressParts.length > 3 ? String.join(" ", Arrays.copyOfRange(addressParts, 3, addressParts.length)) : null);

                shelterRepository.save(shelter);
                log.info("보호소 정보 업데이트 완료 - 보호소명: {}, 시도 코드: {}, 시군구 코드: {}", item.getCareNm(), uprCd, orgCd);
            }
        });
    }

    private String buildPetName(AbandonedPetApiResponse.Item item) {
        String Sex = item.getSexCd().equals("M") ? "(남아)" : item.getSexCd().equals("F") ? "(여아)" : "(성별 미상)" ;
        return String.format("%s %s %s", resolveBreed(item.getKindCd()), item.getAge(), Sex);
    }

    private Species resolveSpecies(String kindCd) {
        return kindCd.contains("[개]") ? Species.DOG : kindCd.contains("[고양이]") ? Species.CAT : Species.OTHER;
    }

    private String resolveBreed(String kindCd) {
        return kindCd.substring(kindCd.indexOf("]") + 1).trim();
    }

    private NeuteredStatus resolveNeuteredStatus(String neuterYn) {
        return switch (neuterYn) {
            case "Y" -> NeuteredStatus.Y;
            case "N" -> NeuteredStatus.N;
            default -> NeuteredStatus.U;
        };
    }

    private Sex resolveSex(String sexCd) {
        return switch (sexCd) {
            case "M" -> Sex.M;
            case "F" -> Sex.F;
            default -> Sex.Q;
        };
    }

    //스케줄링
    @Transactional
    public void refreshAbandonedPetData() {
        log.info("[1단계] abandoned_pet_temp 테이블 초기화 시작");
        jdbcTemplate.execute("TRUNCATE TABLE abandoned_pet_temp");
        log.info("[1단계 완료] abandoned_pet_temp 테이블 초기화 완료");


        if (abandonedPetTempRepository.count() == 0) {
            log.info("[2단계] API 호출 및 데이터 저장 시작");
            fetchAndSaveAbandonedPets(getServiceKey());  // 기존 메서드 재사용
            log.info("[2단계 완료] API 데이터 저장 완료");

            log.info("[3단계] 테이블 이름 교체 시작");
            swapTableNames();
            log.info("[3단계 완료] 테이블 이름 교체 완료");
        }
    }


    private void swapTableNames() {
        jdbcTemplate.execute("ALTER TABLE abandoned_pet RENAME TO abandoned_pet_backup;");
        jdbcTemplate.execute("ALTER TABLE abandoned_pet_temp RENAME TO abandoned_pet;");
        jdbcTemplate.execute("ALTER TABLE abandoned_pet_backup RENAME TO abandoned_pet_temp;");
    }


    private String getServiceKey() {
        return System.getenv("OPENAPI_SERVICE_KEY");
    }
}

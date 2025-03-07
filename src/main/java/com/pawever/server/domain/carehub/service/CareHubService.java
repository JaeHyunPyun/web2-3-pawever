package com.pawever.server.domain.carehub.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.request.SearchShelterRequestDTO;
import com.pawever.server.domain.carehub.dto.response.CareHubResponseDTO;
import com.pawever.server.domain.carehub.dto.response.ShelterPetsResponseDTO;
import com.pawever.server.domain.carehub.dto.response.ShelterResponseDTO;
import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.enums.Sex;
import com.pawever.server.domain.carehub.enums.Species;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import com.pawever.server.domain.carehub.repository.CityCodeRepository;
import com.pawever.server.domain.carehub.repository.DistrictCodeRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CareHubService {
    private final AbandonedPetRepository abandonedPetRepository;
    private final ShelterRepository shelterRepository;
    private final UserRepository userRepository;
    private final CityCodeRepository cityCodeRepository;
    private final DistrictCodeRepository districtCodeRepository;

    //(메인) 유기동물 정보 페이지네이션해서 가져오기 + 필터링 4가지 적용 - 개/고양이 & 시도 & 시군구 & 보호소코드
    public Page<CareHubResponseDTO> getAbandonedPets(int page, int size, String species, String cityName, String districtName, Long shelterId) {
        Pageable pageable = PageRequest.of(page, size);
        // 필터링
        Specification<AbandonedPet> spec = (root, query, cb) -> cb.conjunction();
        // 1. 품종 조회
        if (species != null) {
            log.info("품종 : " + species);
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("species"), Species.valueOf(species)));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ResponseCodeEnum.INVALID_SPECIES);
            }
        }
        // 2. 시도 조회
        if (cityName != null) {
            Long cityCodeId = cityCodeRepository.findCodeByName(cityName);
            if (cityCodeId != null) {
                log.info("시도 코드 : " + cityCodeId);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("cityCode"), cityCodeId));
            } else {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
        }
        // 3. 시군구 조회
        if (districtName != null) {
            if (cityName == null) {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
            Long cityCodeId = cityCodeRepository.findCodeByName(cityName);
            Long districtCodeId = districtCodeRepository.findCodeByName(districtName, cityCodeId);
            Long uprCityCodeId = districtCodeRepository.findUprCodeByName(districtName, cityCodeId);
            log.info("시군구 코드1 : " + districtCodeId);
            log.info("시군구 코드2 : " + cityCodeId);
            log.info("시군구 코드3 : " + uprCityCodeId);
            if (districtCodeId != null && cityCodeId.equals(uprCityCodeId)) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("districtCode"), districtCodeId));
            } else {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
        //4. 보호소 조회
        }
        if (shelterId != null) {
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("providerShelterId"), shelterId));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ResponseCodeEnum.SHELTER_NOT_FOUND);
            }
        }
        Page<AbandonedPet> abandonedPets = abandonedPetRepository.findAll(spec, pageable);

        if (page < 0 || page >= abandonedPets.getTotalPages()) {
            pageable = PageRequest.of(0, size);
            abandonedPets = abandonedPetRepository.findAll(spec, pageable);
        }
        // DTO 변환 후 반환
        return abandonedPets.map(this::convertToCareHubDTO);
    }




    //(입양동물 찾기) 유기동물 정보 페이지네이션해서 가져오기 + 필터링 4가지 적용 - 개/고양이 & 시도 & 시군구 & 보호소코드 & 성별 & 나이 & 검색어
    public Page<CareHubResponseDTO> searchAbandonedPets(int page, int size, String species, String cityName, String districtName, Long shelterId, String sex, String age, String q) {

        Pageable pageable = PageRequest.of(page, size);
        // 필터링
        Specification<AbandonedPet> spec = (root, query, cb) -> cb.conjunction();
        // 1. 품종 조회
        if (species != null) {
            log.info("품종 : " + species);
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("species"), Species.valueOf(species)));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ResponseCodeEnum.INVALID_SPECIES);
            }
        }
        // 2. 시도 조회
        if (cityName != null) {
            Long cityCodeId = cityCodeRepository.findCodeByName(cityName);
            if (cityCodeId != null) {
                log.info("시도 코드 : " + cityCodeId);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("cityCode"), cityCodeId));
            } else {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
        }
        // 3. 시군구 조회
        if (districtName != null) {
            if (cityName == null) {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
            Long cityCodeId = cityCodeRepository.findCodeByName(cityName);
            Long districtCodeId = districtCodeRepository.findCodeByName(districtName, cityCodeId);
            Long uprCityCodeId = districtCodeRepository.findUprCodeByName(districtName, cityCodeId);
            log.info("시군구 코드1 : " + districtCodeId);
            log.info("시군구 코드2 : " + cityCodeId);
            log.info("시군구 코드3 : " + uprCityCodeId);
            if (districtCodeId != null && cityCodeId.equals(uprCityCodeId)) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("districtCode"), districtCodeId));
            } else {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
        }
        //4. 보호소 조회
        if (shelterId != null) {
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("providerShelterId"), shelterId));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ResponseCodeEnum.SHELTER_NOT_FOUND);
            }
        }
        // 5. 성별 조회
        if (sex != null) {
            log.info("품종 : " + sex);
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("sex"), Sex.valueOf(sex)));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ResponseCodeEnum.INVALID_SEX);
            }
        }

        //6. 나이 조회
        if (age != null) {
            log.info("나이 필터링 조건: " + age);

            int currentYear = Year.now().getValue(); // 현재 연도 가져오기

            spec = spec.and((root, query, cb) -> {
                Expression<Integer> birthYearExpression = cb.function(
                        "SUBSTRING", Integer.class, root.get("age"), cb.literal(1), cb.literal(4)
                );

                switch (age) {
                    case "0": // 1살 이하
                        return cb.lessThanOrEqualTo(cb.diff(currentYear, birthYearExpression), 1);
                    case "1": // 1~3살
                        return cb.between(cb.diff(currentYear, birthYearExpression), 2, 3);
                    case "2": // 4살 이상
                        return cb.greaterThanOrEqualTo(cb.diff(currentYear, birthYearExpression), 4);
                    default:
                        return cb.conjunction(); // 필터 적용 X
                }
            });
        }

        // 7. 검색어 기능
        if (q != null && !q.isEmpty()) {
            log.info("검색어 : " + q);

            spec = spec.and((root, query, cb) -> {
                Root<Shelter> shelterRoot = query.from(Shelter.class);
                Predicate joinCondition = cb.equal(root.get("providerShelterId"), shelterRoot.get("providerShelterId"));

                return cb.and(
                        joinCondition,
                        cb.or(
                                cb.like(root.get("name"), "%" + q + "%"),            // 동물 이름 검색
                                cb.like(root.get("breed"), "%" + q + "%"),           // 품종 검색
                                cb.like(root.get("color"), "%" + q + "%"),           // 색상 검색
                                cb.like(root.get("characteristics"), "%" + q + "%"), // 특징 검색
                                cb.like(shelterRoot.get("name"), "%" + q + "%")      // 보호소 이름 검색
                        )
                );
            });
        }



        Page<AbandonedPet> abandonedPets = abandonedPetRepository.findAll(spec, pageable);

        if (page < 0 || page >= abandonedPets.getTotalPages()) {
            pageable = PageRequest.of(0, size);
            abandonedPets = abandonedPetRepository.findAll(spec, pageable);
        }
        // DTO 변환 후 반환
        return abandonedPets.map(this::convertToCareHubDTO);
    }



    //사용자 반경 distance KM 내 유기동물 조회
    public Page<CareHubResponseDTO> getNearbyAbandonedPets(Long userId, double distance, int page, int size, Species species) {
        // 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));


        // 위치 정보가 없는 경우 예외 처리
        if (user.getLatitude() == null || user.getLongitude() == null) {
            throw new CustomException(ResponseCodeEnum.USER_LOCATION_NOT_FOUND);
        }


        // 반경 distance km 내 보호소 찾기
        List<Shelter> nearbyShelters = shelterRepository.findNearbyShelters(user.getLatitude(), user.getLongitude(), distance);

        if (nearbyShelters.isEmpty()) {
            return Page.empty();
        }

        // 보호소 ID 리스트 추출
        List<Long> shelterIds = nearbyShelters.stream().map(Shelter::getProviderShelterId).toList();

        // 해당 보호소에 있는 유기동물 조회 (페이징 적용)

        Pageable pageable = PageRequest.of(page, size);
        Page<AbandonedPet> abandonedPets = abandonedPetRepository.findByProviderShelterIdsAndSpecies(shelterIds, species, pageable);
        if (page >= abandonedPets.getTotalPages() || page < 0) {
            pageable = PageRequest.of(0, size);
            abandonedPets = abandonedPetRepository.findAll(pageable);
        }
        // DTO 변환
        return abandonedPets.map(this::convertToCareHubDTO);
    }


    //시도, 시군구 정보로 보호소 조회
    @Transactional
    public List<ShelterResponseDTO> searchShelter(SearchShelterRequestDTO.SearchShelterRequest request) {

        //1. 둘다 비어있을 경우
        //2. 시도 X, 시군구 O => 예외처리
        //3. 시도 O, 시군구 X
        //4. 시도 O, 시군구 O

        if (request.cityName() == null || request.cityName().isEmpty()) {
            if (request.districtName() == null || request.districtName().isEmpty()) {
                //1. 둘다 비어있는 경우
                List<Shelter> shelters = shelterRepository.findIsNotNull();

                List<ShelterResponseDTO> shelterResponseDTOList = shelters.stream()
                        .map(this::convertToShelterDTO)
                        .collect(Collectors.toList());
                return shelterResponseDTOList;
            } else {
                //2. 시도 X, 시군구 O => 예외처리
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
        } else if (request.districtName() == null || request.districtName().isEmpty()) {
            //3. 시도 O, 시군구 X
            Long cityCodeId = cityCodeRepository.findCodeByName(request.cityName());
            List<Shelter> shelters = shelterRepository.findByCityCode(cityCodeId);

            List<ShelterResponseDTO> shelterResponseDTOList = shelters.stream()
                    .map(this::convertToShelterDTO)
                    .collect(Collectors.toList());
            return shelterResponseDTOList;
        }

        //4. 시도 O, 시군구 O
        Long cityCodeId = cityCodeRepository.findCodeByName(request.cityName());
        Long districtCodeId = districtCodeRepository.findCodeByName(request.districtName(), cityCodeId);
        Long uprCityCodeId = districtCodeRepository.findUprCodeByName(request.districtName(), cityCodeId);

        if (!cityCodeId.equals(uprCityCodeId)) {
            throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
        }
        List<Shelter> shelters = shelterRepository.findByCityCodeAndDistrictCode(cityCodeId, districtCodeId);

        List<ShelterResponseDTO> shelterResponseDTOList = shelters.stream()
                .map(this::convertToShelterDTO)
                .collect(Collectors.toList());
        return shelterResponseDTOList;
    }




    // 보호소 번호로 해당 보호소의 유기동물들 조회
    public List<ShelterPetsResponseDTO> getShelterPets(Long providerShelterId) {
        List<AbandonedPet> abandonedPets = abandonedPetRepository.findAllByProviderShelterId(providerShelterId);

        List<ShelterPetsResponseDTO> shelters = abandonedPets.stream()
                .map(this::convertToShelterPetDTO)
                .collect(Collectors.toList());
        return shelters;
    }



    //DTO로 변경
    private CareHubResponseDTO convertToCareHubDTO(AbandonedPet pet) {
        Shelter shelter = shelterRepository.findByProviderShelterId(pet.getProviderShelterId());
        return new CareHubResponseDTO(
                pet.getId(),
                pet.getProviderShelterId(),
                shelter.getName(),
                pet.getImageUrl(),
                pet.getName(),
                pet.getNeuteredStatus().name(),
                addCharacteristics(pet)
        );
    }

    private ShelterPetsResponseDTO convertToShelterPetDTO(AbandonedPet pet) {
        Shelter shelter = shelterRepository.findByProviderShelterId(pet.getProviderShelterId());
        return ShelterPetsResponseDTO.builder()
                .id(pet.getId())
                .providerShelterId(pet.getProviderShelterId())
                .providerShelterName(shelter.getName())
                .imageUrl(pet.getImageUrl())
                .name(pet.getName())
                .species(pet.getSpecies().name())
                .breed(pet.getBreed())
                .neuteredStatus(pet.getNeuteredStatus().name())
                .sex(pet.getSex().name())
                .age(pet.getAge())
                .foundLocation(pet.getFoundLocation())
                .weight(pet.getWeight())
                .color(pet.getColor())
                .characteristics(pet.getCharacteristics())
                .noticeNumber(pet.getNoticeNumber())
                .cityCode(pet.getCityCode())
                .districtCode(pet.getDistrictCode())
                .build();
    }

    private ShelterResponseDTO convertToShelterDTO(Shelter shelter) {
        return ShelterResponseDTO.builder()
                .ShelterId(shelter.getProviderShelterId())
                .ShelterName(shelter.getName())  // 보호소 이름을 추가하려면 별도 조회 필요
                .build();
    }



    //특징 추가
    private String[] addCharacteristics(AbandonedPet pet) {
        List<String> characteristicsList = new ArrayList<>();
        characteristicsList.add(pet.getSex().name().equals("M") ? "남" : "여");
        characteristicsList.add(pet.getWeight());

        if (pet.getCharacteristics() != null && !pet.getCharacteristics().isEmpty()) {
            String[] splitCharacteristics = pet.getCharacteristics().split("[.,]"); // . 또는 , 기준으로 나눔
            for (String characteristic : splitCharacteristics) {
                characteristic = characteristic.trim(); // 공백 제거
                if (!characteristic.isEmpty()) {
                    characteristicsList.add(characteristic);
                }
            }
        }

        return characteristicsList.toArray(new String[0]);
    }

    //연도에서 나이 추출
    public Integer extractBirthYear(String age) {
        if (age == null || age.isEmpty()) {
            return null;
        }

        //age 문자열에서 연도 추출
        Pattern pattern = Pattern.compile("(\\d{4})");
        Matcher matcher = pattern.matcher(age);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

}
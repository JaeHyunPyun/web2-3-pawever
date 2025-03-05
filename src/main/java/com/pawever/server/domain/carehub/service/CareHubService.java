package com.pawever.server.domain.carehub.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.response.CareHubResponseDTO;
import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.enums.Species;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import com.pawever.server.domain.carehub.repository.CityCodeRepository;
import com.pawever.server.domain.carehub.repository.DistrictCodeRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CareHubService {
    private final AbandonedPetRepository abandonedPetRepository;
    private final ShelterRepository shelterRepository;
    private final UserRepository userRepository;
    private final CityCodeRepository cityCodeRepository;
    private final DistrictCodeRepository districtCodeRepository;

    //(메인) 유기동물 정보 페이지네이션해서 가져오기 + 필터링 3가지 적용 - 개/고양이 & 시도 & 시군구
    public Page<CareHubResponseDTO> getAbandonedPets(int page, int size, String species, String cityName, String districtName) {


        Pageable pageable = PageRequest.of(page, size);

        // 필터링
        Specification<AbandonedPet> spec = (root, query, cb) -> cb.conjunction();

        if (species != null) {
            log.info("품종 : " + species);
            try {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("species"), Species.valueOf(species)));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ResponseCodeEnum.INVALID_SPECIES);
            }
        }
        if (cityName != null) {
            Long cityCodeId = cityCodeRepository.findCodeByName(cityName);
            if (cityCodeId != null) {
                log.info("시도 코드 : " + cityCodeId);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("cityCode"), cityCodeId));
            } else {
                throw new CustomException(ResponseCodeEnum.DISTRICT_NOT_FOUND);
            }
        }
        if (cityName != null && districtName != null) {
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

        Page<AbandonedPet> abandonedPets = abandonedPetRepository.findAll(spec, pageable);


        if (page < 0 || page >= abandonedPets.getTotalPages()) {
            pageable = PageRequest.of(0, size);
            abandonedPets = abandonedPetRepository.findAll(spec, pageable);
        }

        // DTO 변환 후 반환
        return abandonedPets.map(this::convertToDTO);
    }

    //유기동물 정보 페이지네이션해서 가져오기 (아직은 필터링 X)
    public Page<CareHubResponseDTO> searchAbandonedPets(int page, int size) {


        Pageable pageable = PageRequest.of(page, size);

        Page<AbandonedPet> abandonedPets = abandonedPetRepository.findAll(pageable);

        if (page >= abandonedPets.getTotalPages() || page < 0) {
            pageable = PageRequest.of(0, size);
            abandonedPets = abandonedPetRepository.findAll(pageable);
        }

        // DTO 변환 후 반환
        return abandonedPets.map(this::convertToDTO);
    }



    //사용자 반경 distance KM 내 유기동물 조회
    public Page<CareHubResponseDTO> getNearbyAbandonedPets(Long userId, double distance, int page, int size, Species species) {
        // 사용자 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));

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
        return abandonedPets.map(this::convertToDTO);
    }


    private CareHubResponseDTO convertToDTO(AbandonedPet pet) {
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
}
package com.pawever.server.domain.carehub.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.dto.response.AbandonedPetDetailResponse;
import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AbandonedPetDetailService {

    private final AbandonedPetRepository abandonedPetRepository;
    private final ShelterRepository shelterRepository;

    @Transactional(readOnly = true)
    public AbandonedPetDetailResponse getAbandonedPetDetail(Long petId) {

        AbandonedPet pet = abandonedPetRepository.findById(petId)
                .orElseThrow(()-> new CustomException(ResponseCodeEnum.ANIMAL_NOT_FOUND));

        Shelter shelter = shelterRepository.findByProviderShelterId(pet.getProviderShelterId())
                .orElseThrow(() ->new CustomException(ResponseCodeEnum.SHELTER_NOT_FOUND));


        return AbandonedPetDetailResponse.builder()
                .name(pet.getName())
                .id(pet.getId())
                .neuteredStatus(pet.getNeuteredStatus().name())
                .weight(pet.getWeight())
                .color(pet.getColor())
                .characteristics(pet.getCharacteristics())
                .imageUrl(pet.getImageUrl())
                .shelterName(shelter.getName())
                .shelterPhoneNumber(shelter.getCenterPhoneNumber())
                .shelterRoadAddress(shelter.getRoadAddress())
                .latitude(shelter.getLatitude())
                .longitude(shelter.getLongitude())
                .build();
    }
}

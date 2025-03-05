package com.pawever.server.domain.LikedPet.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.LikedPet.dto.LikedPetResponse;
import com.pawever.server.domain.LikedPet.entity.LikedPet;
import com.pawever.server.domain.LikedPet.entity.LikedPetId;
import com.pawever.server.domain.LikedPet.repository.LikedPetRepository;
import com.pawever.server.domain.carehub.entity.AbandonedPet;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import com.pawever.server.domain.carehub.repository.ShelterRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikedPetService {

    private final LikedPetRepository likedPetRepository;
    private final AbandonedPetRepository abandonedPetRepository;
    private final ShelterRepository shelterRepository;
    private final UserRepository userRepository;


    @Transactional
    public boolean toggleLike(Long userId, Long animalId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));

        AbandonedPet abandonedPet = abandonedPetRepository.findById(animalId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.PET_NOT_FOUND)); // 적절한 에러 코드로 수정

        LikedPetId id = new LikedPetId(userId, animalId);
        Optional<LikedPet> likedPetOptional = likedPetRepository.findById(id);

        if (likedPetOptional.isPresent()) {

            likedPetRepository.delete(likedPetOptional.get());
            return false; // 좋아요 취소됨
        } else {

            LikedPet likedPet = new LikedPet(userId, animalId);
            likedPetRepository.save(likedPet);
            return true; // 좋아요 추가됨
        }
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long animalId) {
        return likedPetRepository.existsById(new LikedPetId(userId, animalId));
    }

    @Transactional(readOnly = true)
    public List<LikedPetResponse> getLikedAnimals(Long userId) {
        User user = getUserById(userId);
        Set<Long> petIds = getLikedPetIds(userId);
        List<AbandonedPet> abandonedPets = abandonedPetRepository.findAllById(petIds);

        Map<Long, Shelter> shelterMap = getSheltersByAbandonedPets(abandonedPets);

        return abandonedPets.stream()
                .map(abandonedPet -> createLikedPetResponse(abandonedPet, shelterMap, user))
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCodeEnum.USER_NOT_FOUND));
    }

    private Set<Long> getLikedPetIds(Long userId) {
        return likedPetRepository.findByIdUserId(userId).stream()
                .map(likedPet -> likedPet.getId().getAbandonedPetId())
                .collect(Collectors.toSet());
    }

    private Map<Long, Shelter> getSheltersByAbandonedPets(List<AbandonedPet> abandonedPets) {
        Set<Long> shelterIds = abandonedPets.stream()
                .map(AbandonedPet::getProviderShelterId)
                .collect(Collectors.toSet());

        return shelterRepository.findByProviderShelterIdIn(shelterIds).stream()
                .collect(Collectors.toMap(Shelter::getProviderShelterId, shelter -> shelter));
    }

    private LikedPetResponse createLikedPetResponse(AbandonedPet abandonedPet, Map<Long, Shelter> shelterMap, User user) {
        Shelter shelter = shelterMap.get(abandonedPet.getProviderShelterId());
        if (shelter == null) {
            throw new CustomException(ResponseCodeEnum.SHELTER_NOT_FOUND);
        }

        double distance = calculateDistance(user.getLatitude(), user.getLongitude(),
                shelter.getLatitude(), shelter.getLongitude());

        return LikedPetResponse.builder()
                .id(abandonedPet.getId())
                .name(abandonedPet.getName())
                .age(abandonedPet.getAge())
                .sex(abandonedPet.getSex())
                .imageUrl(abandonedPet.getImageUrl())
                .shelterName(shelter.getName())
                .distanceToShelter(distance)
                .build();
    }

    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        final int R = 6371; // 지구 반지름 (km)

        double lat1Double = lat1.doubleValue();
        double lon1Double = lon1.doubleValue();
        double lat2Double = lat2.doubleValue();
        double lon2Double = lon2.doubleValue();

        double dLat = Math.toRadians(lat2Double - lat1Double);
        double dLon = Math.toRadians(lon2Double - lon1Double);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1Double)) * Math.cos(Math.toRadians(lat2Double))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

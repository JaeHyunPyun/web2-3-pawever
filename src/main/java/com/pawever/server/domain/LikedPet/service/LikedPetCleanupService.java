package com.pawever.server.domain.LikedPet.service;

import com.pawever.server.domain.LikedPet.repository.LikedPetRepository;
import com.pawever.server.domain.carehub.repository.AbandonedPetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
class LikedPetCleanupService {

    @Autowired
    private LikedPetRepository likedPetRepository;

    @Autowired
    private AbandonedPetRepository abandonedPetRepository;

    // AbandonedPet 초기화 후 10분 뒤에 실행 (00:10)
    @Scheduled(cron = "0 10 0 * * *")
    public void cleanupOrphanedLikedPets() {
        try {
            // 현재 존재하는 AbandonedPet의 ID 목록 가져오기
            List<Long> existingAbandonedPetIds = abandonedPetRepository.findAllIds();

            // 존재하지 않는 AbandonedPet을 참조하는 LikedPet 찾아서 삭제
            int deletedCount = likedPetRepository.deleteByAbandonedPetIdNotIn(existingAbandonedPetIds);

            log.info("고아 상태의 LikedPet {} 개 정리 완료", deletedCount);
        } catch (Exception e) {
            log.error("LikedPet 정리 중 오류 발생", e);
        }
    }
}
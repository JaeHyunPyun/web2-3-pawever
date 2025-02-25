package com.pawever.server.domain.carehub.scheduler;

import com.pawever.server.domain.carehub.service.AbandonedPetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedPetScheduler {

    private final AbandonedPetService abandonedPetService;

    @Scheduled(cron = "0 0 0 * * *")  // 매일 00:00 실행
//    @Scheduled(cron = "0 57 14 * * *") // 테스트용
    public void refreshAbandonedPetData() {
        log.info("[스케줄러 시작] 유기동물 데이터 새로고침 시작");

        abandonedPetService.refreshAbandonedPetData();  // 서비스 메서드 호출

        log.info("[스케줄러 완료] 유기동물 데이터 새로고침 완료");
    }
}
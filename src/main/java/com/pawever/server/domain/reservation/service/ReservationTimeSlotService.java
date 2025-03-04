package com.pawever.server.domain.reservation.service;

import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.service.ShelterService;
import com.pawever.server.domain.reservation.entity.ReservationTimeSlot;
import com.pawever.server.domain.reservation.repository.ReservationTimeSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationTimeSlotService {
    //repository
    private final ReservationTimeSlotRepository reservationTimeSlotRepository;

    //service
    private final ShelterService shelterService;

    //보호소 관리자 가입 시 예약 시간 슬롯 생성
    @Transactional
    public void createReservationTimeSlotForShelter(Long shelterID){

        List<ReservationTimeSlot> reservationTimeSlotList = new ArrayList<>();

        List<LocalTime> timeSlotList = List.of(  //한 시간 단위 임의 설정
                LocalTime.of(9, 0),
                LocalTime.of(10,0),
                LocalTime.of(11, 0),
                LocalTime.of(13,0),
                LocalTime.of(14, 0),
                LocalTime.of(15,0),
                LocalTime.of(16, 0),
                LocalTime.of(17,0),
                LocalTime.of(18,0));

        Shelter shelter = shelterService.findShelterByShelterId(shelterID);

        timeSlotList.forEach(t->reservationTimeSlotList.add(ReservationTimeSlot.of(shelter,1,t)));

        reservationTimeSlotRepository.saveAll(reservationTimeSlotList);
    }

    public List<ReservationTimeSlot> findAllByShelter(Shelter shelter){
        return reservationTimeSlotRepository.findAllByShelter(shelter);
    }

    public ReservationTimeSlot findReservationTimeSlotByShelterAndTime(Shelter shelter, LocalTime time){

        List<ReservationTimeSlot> reservationTimeSlotList =  reservationTimeSlotRepository.findAllByShelter(shelter);
        if(reservationTimeSlotList.isEmpty()){throw new CustomException(ResponseCodeEnum.SHELTER_NOT_REGISTERED);}
        return reservationTimeSlotRepository.findByTimeSlotAndShelter(time,shelter).orElseThrow(()-> new CustomException(ResponseCodeEnum.INVALID_RESERVATION_TIME));
    }


}

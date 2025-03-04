package com.pawever.server.domain.reservation.service;

import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.service.ShelterService;
import com.pawever.server.domain.reservation.dto.out.ReservationTimeResponseDto;
import com.pawever.server.domain.reservation.entity.ReservationTimeSlot;
import com.pawever.server.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    //repository
    private final ReservationRepository reservationRepository;

    //service
    private final ShelterService shelterService;
    private final ReservationTimeSlotService reservationTimeSlotService;

    
    public ReservationTimeResponseDto getShelterReservationTime(Long shelterId, LocalDate date){

        Map<ReservationTimeSlot,Boolean> map = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        Shelter shelter = shelterService.findShelterByShelterId(shelterId); // 없으면 shelter not found exception 발생

        List<ReservationTimeSlot> reservationTimeSlotList =  reservationTimeSlotService.findAllByShelter(shelter); //보호소의 예약 시간대(slot) 조회

        if(reservationTimeSlotList.isEmpty()){return ReservationTimeResponseDto.reservationUnAvailable();} //조회되지 않으면 등록되지 않은 보호소 -> 예약 불가

        reservationTimeSlotList.forEach(r->map.put(r,true));

        List<Object[]> reservationList = reservationRepository.findAllByVisitDateAndReservationTimeSlotIds(date,reservationTimeSlotList.stream().map(ReservationTimeSlot::getReservationTimeSlotId).toList());

        for(Object[] reservation : reservationList){
            ReservationTimeSlot temp = (ReservationTimeSlot) reservation[0];
            if(temp.getMaxCapacity()<=(long)reservation[1]) {
                map.put(temp, false);
            }
        }

        List<ReservationTimeResponseDto.TimeSlotDto> timeSlotDtoList = reservationTimeSlotList
                .stream()
                .sorted(Comparator.comparingInt(r->r.getTimeSlot().toSecondOfDay()))
                .map(r->ReservationTimeResponseDto.TimeSlotDto.of(r.getTimeSlot().format(formatter),map.get(r)))
                .toList();

        return ReservationTimeResponseDto.reservationAvailable(timeSlotDtoList);
    }
}
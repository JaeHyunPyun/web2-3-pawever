package com.pawever.server.domain.reservation.service;

import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.carehub.service.ShelterService;
import com.pawever.server.domain.reservation.dto.in.ReservationShelterVisitRequestDTO;
import com.pawever.server.domain.reservation.dto.out.ReservationResponseDTO;
import com.pawever.server.domain.reservation.dto.out.ReservationResultResponseDTO;
import com.pawever.server.domain.reservation.dto.out.ReservationTimeResponseDto;
import com.pawever.server.domain.reservation.entity.Reservation;
import com.pawever.server.domain.reservation.entity.ReservationTimeSlot;
import com.pawever.server.domain.reservation.repository.ReservationRepository;
import com.pawever.server.domain.user.entity.jpa.User;
import com.pawever.server.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final UserService userService;

    
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


    public Boolean isReservationPossible(ReservationTimeSlot reservationTimeSlot,  LocalDate date){
        Integer count = reservationRepository.getCountByVisitDateAndReservationTimeSlotId(date, reservationTimeSlot.getReservationTimeSlotId());
        return  count<reservationTimeSlot.getMaxCapacity();
    }


    @Transactional
    public ReservationResultResponseDTO createReservation(String uuid,ReservationShelterVisitRequestDTO reservationShelterVisitRequestDTO){

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        User user = userService.findUserByUuid(uuid); // 없으면 not found exception

        Shelter shelter = shelterService.findShelterByShelterId(reservationShelterVisitRequestDTO.getShelterId()); // 없으면 not found exception

        ReservationTimeSlot reservationTimeSlot = reservationTimeSlotService.findReservationTimeSlotByShelterAndTime(shelter,reservationShelterVisitRequestDTO.getVisitTime()); //shelter & visit time에 맞는 reservation time slot 조회 -> 없으면 error

        // 에약이 불가능한 경우
        if(!isReservationPossible(reservationTimeSlot,reservationShelterVisitRequestDTO.getVisitDate())){
            return ReservationResultResponseDTO.of(false,shelter.getName(),reservationShelterVisitRequestDTO.getVisitDate().format(dateFormatter),reservationShelterVisitRequestDTO.getVisitTime().format(timeFormatter));
        }

        //에약 가능한 경우
        Reservation reservation = Reservation.createReservation(user,reservationTimeSlot,reservationShelterVisitRequestDTO);
        reservationRepository.save(reservation);

        return ReservationResultResponseDTO.of(true,shelter.getName(),reservationShelterVisitRequestDTO.getVisitDate().format(dateFormatter),reservationShelterVisitRequestDTO.getVisitTime().format(timeFormatter));
    }

    public List<ReservationResponseDTO> findReservationByUser(String uuid, Integer page, Integer size){
        User user = userService.findUserByUuid(uuid);

        Pageable pageable = PageRequest.of(page,size, Sort.by("visitDate").descending());

        return reservationRepository.findAllByUser(user,pageable).getContent()
                .stream()
                .map(ReservationResponseDTO::of)
                .toList();
    }

    public List<ReservationResponseDTO> findReservationByStaff(String uuid, Integer page, Integer size){
        User staff = userService.findUserByUuid(uuid);

        Pageable pageable = PageRequest.of(page,size, Sort.by("r.visitDate").descending());

        return reservationRepository.findAllByShelterStaff(staff,pageable).getContent()
                .stream()
                .map(ReservationResponseDTO::of)
                .toList();
    }
}
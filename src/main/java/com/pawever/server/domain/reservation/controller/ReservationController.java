package com.pawever.server.domain.reservation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.reservation.service.ReservationService;
import com.pawever.server.domain.reservation.service.ReservationTimeSlotService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationTimeSlotService reservationTimeSlotService;


    @GetMapping("/shelters/{shelter_id}")
    public ResponseEntity<ApiResponse> getShelterReservationTime(@PathVariable("shelter_id")Long shelterId, @RequestParam("date") LocalDate date){
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS,reservationService.getShelterReservationTime(shelterId,date)));
    }

    @PostMapping("/shelters/{shelter_id}/timeslot")
    public void registerStaff(@PathVariable(value = "shelter_id")Long shelterId){
        reservationTimeSlotService.createReservationTimeSlotForShelter(shelterId);
    }

}

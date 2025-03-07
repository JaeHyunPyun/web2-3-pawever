package com.pawever.server.domain.reservation.controller;

import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.carehub.service.ShelterService;
import com.pawever.server.domain.reservation.dto.in.ReservationShelterVisitRequestDTO;
import com.pawever.server.domain.reservation.service.ReservationService;
import com.pawever.server.domain.reservation.service.ReservationTimeSlotService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.AccessTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
@Tag(name = "예약 API")
@Slf4j
public class ReservationController {

    //service
    private final ReservationService reservationService;
    private final ReservationTimeSlotService reservationTimeSlotService;
    private final ShelterService shelterService;

    @GetMapping("/shelters/{shelter_id}")
    @Operation(summary = "특정 보호소 예약 조회")
    public ResponseEntity<ApiResponse> getShelterReservationTime(@PathVariable("shelter_id")Long shelterId, @RequestParam("date") LocalDate date){
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS,reservationService.getShelterReservationTime(shelterId,date)));
    }

    @GetMapping("/shelters")
    @Operation(summary = "보호소 예약 조회")
    public ResponseEntity<ApiResponse> getShelters(@RequestParam(value = "page",required = false, defaultValue = "0")Integer pageNo, @RequestParam(value = "size", required = false, defaultValue ="100000")Integer size){
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS,shelterService.findAllShelters(pageNo,size)));
    }

    @PostMapping()
    @Operation(summary = "예약 생성")
    public ResponseEntity<ApiResponse> createReservations(@AuthenticationPrincipal CustomUserDetails customUserDetails , @RequestBody ReservationShelterVisitRequestDTO reservationShelterVisitRequestDTO){
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS,reservationService.createReservation(customUserDetails.getUsername(),reservationShelterVisitRequestDTO)));
    }

    @GetMapping()
    @Operation(summary = "예약 목록 조회")
    public ResponseEntity<ApiResponse> findReservationByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestParam(value = "page",required = false, defaultValue = "0")Integer pageNo, @RequestParam(value = "size", required = false, defaultValue ="100000")Integer size){
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS,reservationService.findReservationByUser(customUserDetails.getUsername(),pageNo,size)));
    }

    @GetMapping("/staff")
    @Operation(summary = "스태프 예약 조회")
    public ResponseEntity<ApiResponse> findReservationByStaff(@AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestParam(value = "page",required = false, defaultValue = "0")Integer pageNo, @RequestParam(value = "size", required = false, defaultValue ="100000")Integer size){
        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS,reservationService.findReservationByStaff(customUserDetails.getUsername(),pageNo,size)));
    }

}

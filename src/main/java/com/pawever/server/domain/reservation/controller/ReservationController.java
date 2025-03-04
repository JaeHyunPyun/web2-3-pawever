package com.pawever.server.domain.reservation.controller;

import com.pawever.server.domain.reservation.service.ReservationService;
import com.pawever.server.domain.reservation.service.ReservationTimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationTimeSlotService reservationTimeSlotService;
}

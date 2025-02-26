package com.pawever.server.domain.reservation.service;

import com.pawever.server.domain.reservation.repository.ReservationTimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationTimeSlotService {
    private ReservationTimeSlotRepository reservationTimeSlotRepository;
}

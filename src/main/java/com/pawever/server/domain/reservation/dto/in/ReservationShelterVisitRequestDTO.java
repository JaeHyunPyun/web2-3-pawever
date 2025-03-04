package com.pawever.server.domain.reservation.dto.in;

import com.pawever.server.domain.reservation.enums.VisitType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ReservationShelterVisitRequestDTO {
    private String title;
    private VisitType visitType;
    private String visitorName;
    private String visitorPhoneNumber;
    private Long shelterId;
    private LocalDate visitDate;
    private LocalTime visitTime;
}

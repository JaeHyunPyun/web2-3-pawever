package com.pawever.server.domain.reservation.dto.out;

import com.pawever.server.domain.reservation.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
public class ReservationResponseDTO {
    private String visitNote;
    private String visitorName;
    private String visitorPhoneNumber;
    private String visitType;
    private String shelterName;
    private String visitDate;
    private String visitTime;

    @Builder
    public ReservationResponseDTO(String visitNote, String visitorName, String shelterName, String visitDate, String visitTime, String visitorPhoneNumber, String visitType) {
        this.visitNote = visitNote;
        this.visitorName = visitorName;
        this.shelterName = shelterName;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.visitorPhoneNumber = visitorPhoneNumber;
        this.visitType = visitType;
    }

    public static ReservationResponseDTO of(Reservation reservation){

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return ReservationResponseDTO.builder()
                .visitNote(reservation.getVisitNote())
                .visitorName(reservation.getVisitorName())
                .shelterName(reservation.getReservationTimeSlot().getShelter().getName())
                .visitDate(reservation.getVisitDate().format(dateFormatter))
                .visitTime(reservation.getReservationTimeSlot().getTimeSlot().format(timeFormatter))
                .visitorPhoneNumber(reservation.getVisitorPhoneNumber())
                .visitType(reservation.getVisitType().name())
                .build();
    }

}

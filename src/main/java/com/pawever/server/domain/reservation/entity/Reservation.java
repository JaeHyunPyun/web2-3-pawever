package com.pawever.server.domain.reservation.entity;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.reservation.dto.in.ReservationShelterVisitRequestDTO;
import com.pawever.server.domain.reservation.enums.VisitType;
import com.pawever.server.domain.user.entity.jpa.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Table(name="reservation")
@Getter
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_time_slot_id")
    private ReservationTimeSlot reservationTimeSlot;

    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    private LocalDate visitDate;

    private String visitorName;

    private String visitorPhoneNumber;

    private String visitNote;


    @Builder
    private Reservation(User user, ReservationTimeSlot reservationTimeSlot,VisitType visitType, LocalDate visitDate, String visitorName, String visitorPhoneNumber, String visitNote ){
        this.user = user;
        this.reservationTimeSlot = reservationTimeSlot;
        this.visitNote = visitNote;
        this.visitorName = visitorName;
        this.visitType = visitType;
        this.visitDate = visitDate;
        this.visitorPhoneNumber = visitorPhoneNumber;

    }



    public static Reservation createReservation(User user,ReservationTimeSlot reservationTimeSlot, ReservationShelterVisitRequestDTO reservationShelterVisitRequestDTO){
         return Reservation.builder()
                 .user(user)
                 .reservationTimeSlot(reservationTimeSlot)
                 .visitNote(reservationShelterVisitRequestDTO.getTitle())
                 .visitorName(reservationShelterVisitRequestDTO.getVisitorName())
                 .visitorPhoneNumber(reservationShelterVisitRequestDTO.getVisitorPhoneNumber())
                 .visitDate(reservationShelterVisitRequestDTO.getVisitDate())
                 .visitType(reservationShelterVisitRequestDTO.getVisitType())
                 .build();
    }

}

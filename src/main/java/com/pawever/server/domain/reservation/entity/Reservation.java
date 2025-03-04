package com.pawever.server.domain.reservation.entity;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.reservation.enums.VisitType;
import com.pawever.server.domain.user.entity.jpa.User;
import jakarta.persistence.*;
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

}

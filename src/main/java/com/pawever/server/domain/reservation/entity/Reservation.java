package com.pawever.server.domain.reservation.entity;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.reservation.enums.VisitType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="reservation")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    @Column(name = "user_id")
    private Long user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_time_slot_id")
    private ReservationTimeSlot reservationTimeSlotId;
    private VisitType visitType;

    private LocalDate visitDate;

    private String visitorName;

    private String visitorPhoneNumber;

    private String visitNote;

}

package com.pawever.server.domain.reservation.entity;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.carehub.entity.Shelter;
import jakarta.persistence.*;

import java.sql.Time;

@Entity
@Table(name="reservation_time_slot")
public class ReservationTimeSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationTimeSlotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shelter_id")
    private Shelter shelter;

    private Time timeSlot;

    private Integer maxCapacity;

}

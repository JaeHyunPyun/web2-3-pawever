package com.pawever.server.domain.reservation.entity;

import com.pawever.server.common.entity.BaseEntity;
import com.pawever.server.domain.carehub.entity.Shelter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@Entity
@RequiredArgsConstructor
@Table(name="reservation_time_slot")
@Getter
public class ReservationTimeSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationTimeSlotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shelter_id")
    private Shelter shelter;

    private LocalTime timeSlot;

    private Integer maxCapacity;

    @Builder
    private ReservationTimeSlot(Integer maxCapacity, LocalTime timeSlot, Shelter shelter){
        this.maxCapacity = maxCapacity;
        this.timeSlot = timeSlot;
        this.shelter = shelter;
    }

    public static ReservationTimeSlot of(Shelter shelter, Integer maxCapacity, LocalTime timeSlot){
        return ReservationTimeSlot
                .builder()
                .timeSlot(timeSlot)
                .shelter(shelter)
                .maxCapacity(maxCapacity)
                .build();
    }
}

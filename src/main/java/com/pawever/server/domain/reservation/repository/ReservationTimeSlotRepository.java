package com.pawever.server.domain.reservation.repository;

import com.pawever.server.domain.reservation.entity.ReservationTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationTimeSlotRepository extends JpaRepository<ReservationTimeSlot,Long> {
}

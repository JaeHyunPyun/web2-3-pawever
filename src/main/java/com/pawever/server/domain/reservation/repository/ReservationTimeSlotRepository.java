package com.pawever.server.domain.reservation.repository;

import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.reservation.entity.ReservationTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationTimeSlotRepository extends JpaRepository<ReservationTimeSlot,Long> {

    List<ReservationTimeSlot> findAllByShelter(Shelter shelter);

}

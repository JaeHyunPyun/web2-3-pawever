package com.pawever.server.domain.reservation.repository;

import com.pawever.server.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    @Query("SELECT r.reservationTimeSlot, COUNT(r) " +
            "FROM Reservation r " +
            "WHERE r.visitDate = :date " +
            "AND r.reservationTimeSlot.reservationTimeSlotId IN :ids " +
            "GROUP BY r.reservationTimeSlot")
    List<Object[]> findAllByVisitDateAndReservationTimeSlotIds(@Param("date")LocalDate date, @Param("ids")List<Long> ids);

    @Query("SELECT COUNT(r) " +
            "FROM Reservation r " +
            "WHERE r.visitDate = :date " +
            "AND r.reservationTimeSlot.reservationTimeSlotId = :id"
    )
    Integer getCountByVisitDateAndReservationTimeSlotId(@Param("date")LocalDate date, @Param("id")Long id);
}

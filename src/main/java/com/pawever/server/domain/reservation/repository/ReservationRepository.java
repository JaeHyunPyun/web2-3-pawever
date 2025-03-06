package com.pawever.server.domain.reservation.repository;

import com.pawever.server.domain.reservation.entity.Reservation;
import com.pawever.server.domain.user.entity.jpa.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"reservationTimeSlot", "reservationTimeSlot.shelter"})
    Page<Reservation> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"reservationTimeSlot", "reservationTimeSlot.shelter"})
    @Query(" SELECT r " +
            " FROM Reservation r " +
            " where r.reservationTimeSlot.shelter.user = :user")
    Page<Reservation> findAllByShelterStaff(@Param("user")User user, Pageable pageable);

}

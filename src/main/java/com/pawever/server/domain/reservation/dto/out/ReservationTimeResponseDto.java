package com.pawever.server.domain.reservation.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Data
public class ReservationTimeResponseDto {

    private Boolean isShelterReservationAvailable;

    private List<TimeSlotDto> reservationTimeList;

    public record TimeSlotDto(String time, Boolean isEnabled){
        public static TimeSlotDto of(String time, Boolean isEnabled){
            return new TimeSlotDto(time,isEnabled);
        }
    }

    @Builder
    private ReservationTimeResponseDto(Boolean isShelterReservationAvailable, List<TimeSlotDto> reservationTimeList){
        this.isShelterReservationAvailable = isShelterReservationAvailable;
        this.reservationTimeList = reservationTimeList;
    }

    public static ReservationTimeResponseDto reservationUnAvailable(){
        return ReservationTimeResponseDto
                .builder()
                .reservationTimeList(null)
                .isShelterReservationAvailable(false)
                .build();
    }

    public static ReservationTimeResponseDto reservationAvailable(List<TimeSlotDto> timeSlots){
        return ReservationTimeResponseDto
                .builder()
                .reservationTimeList(timeSlots)
                .isShelterReservationAvailable(true)
                .build();
    }

}

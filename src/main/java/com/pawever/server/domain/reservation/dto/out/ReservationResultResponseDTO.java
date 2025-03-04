package com.pawever.server.domain.reservation.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationResultResponseDTO {

    private Boolean reservationSuccess;
    private String shelterName;
    private String visitDate;
    private String visitTime;

    public static ReservationResultResponseDTO of(Boolean reservationSuccess, String shelterName, String visitDate, String visitTime){
        return new ReservationResultResponseDTO(reservationSuccess,shelterName,visitDate,visitTime);
    }

}

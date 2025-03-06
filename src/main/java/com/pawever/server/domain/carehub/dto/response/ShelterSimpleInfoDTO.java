package com.pawever.server.domain.carehub.dto.response;

import com.pawever.server.domain.carehub.entity.Shelter;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ShelterSimpleInfoDTO {

    private Long shelterId;

    private String shelterName;

    private String centerPhoneNumber;

    private String address;

    private BigDecimal latitude;  // 위도

    private BigDecimal longitude;  // 경도

    @Builder
    private ShelterSimpleInfoDTO(Long shelterId, String shelterName, String centerPhoneNumber, BigDecimal latitude, BigDecimal longitude, String address ){
        this.shelterId = shelterId;
        this.shelterName = shelterName;
        this.centerPhoneNumber = centerPhoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public static ShelterSimpleInfoDTO of(Shelter shelter){

        String sido = shelter.getSido()==null?"":shelter.getSido()+" ";
        String sigungu = shelter.getSigungu()==null?"":shelter.getSigungu()+" ";
        String eupmyeondong = shelter.getEupmyeondong()==null?"":shelter.getEupmyeondong()+" ";
        String roadAddress = shelter.getRoadAddress()==null?"":shelter.getRoadAddress();

        String address = sido+sigungu+eupmyeondong+roadAddress;

        return ShelterSimpleInfoDTO.builder()
                .shelterId(shelter.getId())
                .shelterName(shelter.getName())
                .centerPhoneNumber(shelter.getCenterPhoneNumber())
                .latitude(shelter.getLatitude().compareTo(BigDecimal.ZERO) == 0?null:shelter.getLatitude())
                .longitude(shelter.getLongitude().compareTo(BigDecimal.ZERO) == 0?null:shelter.getLongitude())
                .address(address.isEmpty() ?null:address)
                .build();
    }

}

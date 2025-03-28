package com.pawever.server.domain.user.service;

import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import com.pawever.server.common.infra.IpGeoLocationProvider;
import com.pawever.server.domain.user.dto.response.IpGeoLocationDto;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IpGeoLocationService {

    private final IpGeoLocationProvider ipGeoLocationProvider;

    public Optional<IpGeoLocationDto> getGeoLocationByIp(InetAddress inetAddress) {
        // 실제 코드
//        CityResponse ipBasedGeoLocation = ipGeoLocationProvider.getGeoLocationByIp(inetAddress);

        // 테스트시 활성화 코드
        InetAddress testInetAddress = null;
        try {
            testInetAddress = InetAddress.getByName("128.101.101.101");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        CityResponse ipBasedGeoLocation = ipGeoLocationProvider.getGeoLocationByIp(testInetAddress);
        ////////////////////////////////////////////////////

        if(ipBasedGeoLocation == null) {
            return Optional.empty();
        }

        Country ipBasedGeoCountry = ipBasedGeoLocation.getCountry();
        Subdivision ipBasedGeoSubdivision = ipBasedGeoLocation.getMostSpecificSubdivision();
        City ipBasedGeoCity = ipBasedGeoLocation.getCity();

        return Optional.of(IpGeoLocationDto.builder()
            .countryName(ipBasedGeoCountry.getName())
            .subdivisionName(ipBasedGeoSubdivision.getName())
            .cityName(ipBasedGeoCity.getName())
            .build());
    }
}

package com.pawever.server.common.infra;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IpGeoLocationProvider {

    private final DatabaseReader ipGeoDatabaseReader;

    public IpGeoLocationProvider() throws IOException {
        // 클래스패스에서 파일을 읽는다
        ClassPathResource classPathResource = new ClassPathResource("data/GeoLite2-City.mmdb");
        try (InputStream dbStream = classPathResource.getInputStream()) {
            ipGeoDatabaseReader = new DatabaseReader.Builder(dbStream).build();
        }
    }

    public CityResponse getGeoLocationByIp(InetAddress inetAddress) {
        CityResponse ipBasedGeoLocation = null;
        try {
            ipBasedGeoLocation = ipGeoDatabaseReader.city(inetAddress);
        } catch (IOException e) {
            log.error("[IOException] {}의 지역 정보를 가져오는데 실패하였습니다.", inetAddress, e);
        } catch (GeoIp2Exception e) {
            log.error("[GeoIp2Exception] {}의 지역 정보를 가져오는데 실패하였습니다.", inetAddress, e);
        }
        return ipBasedGeoLocation;
    }
}

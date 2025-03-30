package com.pawever.server.common.infra;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        InputStream dbStream = classPathResource.getInputStream();

        // 임시 파일 생성 (앱 종료 시 자동 삭제)
        File tempFile = File.createTempFile("GeoLite2-City", ".mmdb");
        tempFile.deleteOnExit();
        Files.copy(dbStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // 임시 파일을 DatabaseReader에 넘김
        this.ipGeoDatabaseReader = new DatabaseReader.Builder(tempFile).build();
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

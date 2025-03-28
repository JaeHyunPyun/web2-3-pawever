package com.pawever.server.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ClientInfoResolver {

    public InetAddress getClientIp(HttpServletRequest request) {
        String clientIp = null;
        InetAddress inetAddress = null;
        boolean isIpInHeader = false;

        // 1. 프록시/로드밸러서 사용시 클라이언트의 ip 주소 반환
        List<String> headerList = new ArrayList<>();
        headerList.add("X-Forwarded-For");
        headerList.add("HTTP_CLIENT_IP");
        headerList.add("HTTP_X_FORWARDED_FOR");
        headerList.add("HTTP_X_FORWARDED");
        headerList.add("HTTP_FORWARDED_FOR");
        headerList.add("HTTP_FORWARDED");
        headerList.add("Proxy-Client-IP");
        headerList.add("WL-Proxy-Client-IP");
        headerList.add("HTTP_VIA");
        headerList.add("IPV6_ADR");

        for (String header : headerList) {
            clientIp = request.getHeader(header);
            if (StringUtils.hasText(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
                isIpInHeader = true;
                break;
            }
        }

        // 2. 프록시/로드밸런서 없이 클라이언트->서버 직접 접속시 클라이언트 ip 주소 반환
        if (!isIpInHeader) {
            clientIp = request.getRemoteAddr();
        }

//         3. 로컬호스트에서 접근시 정확한 ip 추출을 위한 코드
        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
            InetAddress localHostInetAddress = null;
            try {
                localHostInetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("[UnknownHostException] 클라이언트의 Ip주소를 확인할 수 없습니다 : {}",clientIp, e);
            }
            clientIp = localHostInetAddress.getHostAddress();
        }

        // 4. String ip -> InetAddress 변환
        try {
            inetAddress = InetAddress.getByName(clientIp);
        } catch (UnknownHostException e) {
            log.error("[UnknownHostException] 클라이언트의 Ip주소를 확인할 수 없습니다 : {}",clientIp, e);
        }

        return inetAddress;
    }
    public String getClientBrowser(HttpServletRequest request) {
        String lowerCasedClientAgent = getLowerCasedClientAgent(request);
        String clientBrowser = "";

        if (lowerCasedClientAgent.contains("trident")) {        //IE
            clientBrowser = "IE";
        }
        else if (lowerCasedClientAgent.contains("edge")) {      //Edge
            clientBrowser = "Edge";
        }
        else if (lowerCasedClientAgent.contains("whale")) {      //Naver Whale
            clientBrowser = "Naver Whale";
        }
        else if (lowerCasedClientAgent.contains("opera")||lowerCasedClientAgent.contains("opr")) {      //Opera
            clientBrowser = "Opera";
        }
        else if (lowerCasedClientAgent.contains("chrome")) {       //Chrome
            clientBrowser = "Chrome";
        }
        else if (!lowerCasedClientAgent.contains("chrome") && lowerCasedClientAgent.contains("safari")) {     //Safari
            clientBrowser = "Safari";
        }
        else if (lowerCasedClientAgent.contains("firefox")) {              //Firefox
            clientBrowser = "Firefox";
        }
        else {
            clientBrowser ="Other";
        }

        return clientBrowser;
    }
    public String getClientOs(HttpServletRequest request) {
        String lowerCasedClientAgent = getLowerCasedClientAgent(request);
        String clientOs = "";
        if (lowerCasedClientAgent.contains("windows nt 10.0")) {
            clientOs = "Windows10";
        }else if (lowerCasedClientAgent.contains("windows nt 6.1")) {
            clientOs = "Windows7";
        }else if (lowerCasedClientAgent.contains("windows nt 6.2") || lowerCasedClientAgent.contains("windows nt 6.3")) {
            clientOs = "Windows8";
        }else if (lowerCasedClientAgent.contains("windows nt 6.0")) {
            clientOs = "WindowsVista";
        }else if (lowerCasedClientAgent.contains("windows nt 5.1")) {
            clientOs = "WindowsXP";
        }else if (lowerCasedClientAgent.contains("windows nt 5.0")) {
            clientOs = "Windows2000";
        }else if (lowerCasedClientAgent.contains("windows nt 4.0")) {
            clientOs = "WindowsNT";
        }else if (lowerCasedClientAgent.contains("windows 98")) {
            clientOs = "Windows98";
        }else if (lowerCasedClientAgent.contains("windows 95")) {
            clientOs = "Windows95";
        }else if (lowerCasedClientAgent.contains("iphone")) {
            clientOs = "iPhone";
        }else if (lowerCasedClientAgent.contains("ipad")) {
            clientOs = "iPad";
        }else if (lowerCasedClientAgent.contains("android")) {
            clientOs = "android";
        }else if (lowerCasedClientAgent.contains("mac")) {
            clientOs = "mac";
        }else if (lowerCasedClientAgent.contains("linux")) {
            clientOs = "Linux";
        }else{
            clientOs = "Other";
        }
        return clientOs;
    }
    public String getLowerCasedClientAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent").toLowerCase();
    }

}

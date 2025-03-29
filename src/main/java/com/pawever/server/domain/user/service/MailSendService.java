package com.pawever.server.domain.user.service;

import com.pawever.server.domain.user.dto.internal.LoginSecurityMailSendDto;
import com.pawever.server.domain.user.dto.response.IpGeoLocationDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class MailSendService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final IpGeoLocationService ipGeoLocationService;
    private final ClientInfoResolver clientInfoResolver;

    public MailSendService(JavaMailSender mailSender, TemplateEngine templateEngine, IpGeoLocationService ipGeoLocationService, ClientInfoResolver clientInfoResolver) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.ipGeoLocationService = ipGeoLocationService;
        this.clientInfoResolver = clientInfoResolver;
    }

    public void sendLoginSecurityMail(LoginSecurityMailSendDto loginSecurityMailSendDto, HttpServletRequest request) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 1. MimeMessage 객체 생성을 돕는 MimeMessageHelper객체 생성
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 2. Context 객체는 Thymeleaf 템플릿엔진에 데이터를 전달하는 역할 수행
            Context context = new Context();
            context.setVariable("subject", loginSecurityMailSendDto.getSubject());   // 메일 제목
            context.setVariable("loginTime", ZonedDateTime.now(ZoneId.of("Asia/Seoul"))); // 로그인 시간(한국 시간 적용)
            InetAddress clientInetAdderess = clientInfoResolver.getClientIp(request);

            // todo : 사용자 ip를 확인할 수 없는 경우 메일 전송 x -> 좀 더 세련되게 처리할 수 있는 방법이 있는지 체크
            if(clientInetAdderess == null) {
                return;
            }
            String clientIp = clientInetAdderess.getHostAddress();
            context.setVariable("clientIp", clientIp); // 로그인 ip

            String countryName = ipGeoLocationService.getGeoLocationByIp(clientInetAdderess)
                .map(IpGeoLocationDto::getCountryName)
                .orElse("국가명 조회 불가");

            context.setVariable("clientLocation",   countryName);  // 로그인 지역
            context.setVariable("clientOs", clientInfoResolver.getClientOs(request));   // 로그인 os
            context.setVariable("clientBrowser", clientInfoResolver.getClientBrowser(request));  // 로그인 browser

            // todo : login-security-mail html에서 userType을 userName로 변경해주기
            context.setVariable("userName", maskUserName(loginSecurityMailSendDto.getUserName()));  // 사용자 User 아이디(masking 추가)

            // 3. "email-template.html"을 Thymeleaf 엔진을 통해 렌더링
            String htmlContent = templateEngine.process("login-security-mail", context);

            // 4. MimeMessageHelper 객체를 통해 이메일 발송
            mimeMessageHelper.setTo(loginSecurityMailSendDto.getEmailAddr());            // 수신자 이메일 설정
            mimeMessageHelper.setSubject(loginSecurityMailSendDto.getSubject());        // 이메일 제목 설정
            mimeMessageHelper.setText(htmlContent, true);                       // HTML 본문 설정(true는 Html 형식 사용을 의미)
            mimeMessageHelper.addInline("image", new ClassPathResource("static/images/pawever_mail_image.png"));

            log.info("[메일 전송 시작] userId : {}", loginSecurityMailSendDto.getUserName());
            mailSender.send(mimeMessage);               // 이메일 전송
            log.info("[메일 전송 완료] userId : {}", loginSecurityMailSendDto.getUserName());
        } catch (MessagingException e) {
            log.info("[메일 전송 실패] userId : {}, errorMessage : {}", loginSecurityMailSendDto.getUserName(), e.getMessage());
        }
    }

    private String maskUserName(String originalUserName) {
        int visibleUserNameLength = (int)Math.ceil(originalUserName.length()/2.0f);  // 전체 글자의 50%만 노출
        int invisibleUserNameLength = originalUserName.length()-visibleUserNameLength;

        StringBuilder maskedUserName = new StringBuilder();
        maskedUserName.append(originalUserName, 0, visibleUserNameLength);
        maskedUserName.append("*".repeat(Math.max(0, invisibleUserNameLength)));

        return maskedUserName.toString();
    }

}

package com.pawever.server.domain.user.controller;


import com.pawever.server.common.exception.CustomException;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.post.service.ImageService;
import com.pawever.server.domain.user.dto.request.UserProfileUpdateRequestDto;
import com.pawever.server.domain.user.dto.response.UserProfileResponseDto;
import com.pawever.server.common.response.ApiResponse;
import com.pawever.server.common.response.ResponseCodeEnum;
import com.pawever.server.domain.reservation.service.ReservationService;
import com.pawever.server.domain.user.dto.response.CustomUserDetails;
import com.pawever.server.domain.user.enums.Role;
import com.pawever.server.domain.user.jwt.JwtUtil;
import com.pawever.server.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ImageService imageService;

    @GetMapping("/admin")
    public String admin(HttpServletRequest request) {
        // admin 권한 확인용 테스트 컨트롤러입니다.

        String accessTokenWithBearer= request.getHeader("Authorization");
        log.info("accessTokenWithBearer: " + accessTokenWithBearer);

        String accessToken = accessTokenWithBearer.split(" ")[1];
        String category = jwtUtil.getCategory(accessToken);
        String socialLoginUuid = jwtUtil.getSocialLoginUuid(accessToken);
        Role role = jwtUtil.getRole(accessToken);

        log.info("category: "+category);
        log.info("socialLoginUuid: "+socialLoginUuid);
        log.info("role: "+role.name());

        return "admin authority confirmed";
    }

    @DeleteMapping("/profiles")
    public ResponseEntity<?> withdraw(HttpServletRequest request, HttpServletResponse response){

        // 1. 로그아웃(서버 Refresh토큰 삭제 + 쿠키 초기화)
        userService.logout(request, response);

        // 2. 회원정보 삭제
        userService.softDeleteUserByUuid(request);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }


    @GetMapping("/profiles")
    public ResponseEntity<UserProfileResponseDto> getUserProfiles(HttpServletRequest request){
        return ResponseEntity
            .ok(userService.getUserProfiles(request));  // 200(ok) + UserResponseDto 반환
    }

    @GetMapping("/upload/defaultimages")
    public ResponseEntity<String> uploadDefaultUserImage(@RequestParam("file") MultipartFile file){
        // user 디폴트 이미지를 s3에 올리고 링크를 반환받는 api
        // 반환되는 객체 uri를 회원가입시 이미지가 없는 유저 profile image url에 매핑
        if(file.isEmpty()){
            // 500(Internal Server Error)
            throw new CustomException(ResponseCodeEnum.FILE_READ_ERROR);
        }
        return ResponseEntity.ok(imageService.uploadImageToS3(file));
    }

    @PatchMapping(value="/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateUserProfile(
        @RequestPart(value = "data", required = false) UserProfileUpdateRequestDto userProfileUpdateRequestDto,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile,
        HttpServletRequest request
    ) {
        userService.updateUserProfile(userProfileUpdateRequestDto, profileImageFile, request);

        return ResponseEntity.noContent().build(); // 성공시 204 코드 반환(Response body 없음)
    }
}

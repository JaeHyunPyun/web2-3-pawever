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
import com.pawever.server.domain.user.service.AccessTokenService;
import com.pawever.server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "유저 API", description = "사용자 프로필 및 권한 관리 API")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ImageService imageService;
    private final AccessTokenService accessTokenService;

    @GetMapping("/admin")
    @Operation(summary = "admin 권한 확인용 테스트 API")
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
    @Operation(summary = "회원 탈퇴 API")
    public ResponseEntity<?> withdraw(HttpServletRequest request, HttpServletResponse response){

        // 1. 로그아웃(서버 Refresh토큰 삭제 + 쿠키 초기화)
        userService.logout(request, response);

        // 2. 회원정보 삭제
        userService.softDeleteUserByUuid(request);

        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    @GetMapping("/profiles")
    @Operation(summary = "사용자 프로필 조회 API")
    public ResponseEntity<ApiResponse> getUserProfiles(HttpServletRequest request){
        return ResponseEntity
            .ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, userService.getUserProfiles(request)));
    }

    @GetMapping("/upload/defaultimages")
    @Operation(summary = "기본 프로필 이미지 조회 API")
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
    @Operation(summary = "사용자 프로필 수정 API")
    public ResponseEntity<ApiResponse> updateUserProfile(
        @RequestPart(value = "data", required = false) UserProfileUpdateRequestDto userProfileUpdateRequestDto,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile,
        HttpServletRequest request
    ) {
        userService.updateUserProfile(userProfileUpdateRequestDto, profileImageFile, request);

        return ResponseEntity
            .ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

    @GetMapping("/staffs")
    @Operation(summary = "스태프 목록 조회")
    public ResponseEntity<ApiResponse> getStaffProfiles(HttpServletRequest request){
        return ResponseEntity
            .ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, userService.getStaffProfiles(request)));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getUserRoles(HttpServletRequest request){
        String accessToken = accessTokenService.getRequestAccessToken(request);
        Role userRole = jwtUtil.getRole(accessToken);

        return ResponseEntity.ok(ApiResponse.success(ResponseCodeEnum.SUCCESS, userRole));
    }

    @PatchMapping("/roles")
    public ResponseEntity<ApiResponse> updateUserRoles(@RequestParam("role") String inputRole, HttpServletRequest request){

        userService.updateUserRoles(inputRole, request);

        return ResponseEntity
            .ok(ApiResponse.success(ResponseCodeEnum.SUCCESS));
    }

}

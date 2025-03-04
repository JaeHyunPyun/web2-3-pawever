package com.pawever.server.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCodeEnum {

    // 성공 코드
    SUCCESS(HttpStatus.OK,"SUCCESS_0","요청에 성공"),
    CREATED(HttpStatus.CREATED,"SUCCESS_1","새로운 resource가 생성됨"),
    NO_CONTENT(HttpStatus.NO_CONTENT,"SUCCESS_2","반환할 응답값이 없음"),

    //요청관련 에러
    INVALID_REQUEST_ARGUMENT(HttpStatus.BAD_REQUEST,"REQUEST_0","부적절한 request argument가 전달됨"),
    NO_REQUEST_ARGUMENT(HttpStatus.BAD_REQUEST,"REQUEST_1","request가 요청에 포함되지 않았습니다."),

    // 인증 관련 에러
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_0", "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_1", "JWT 토큰이 유효하지 않습니다."),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "AUTH_2", "권한이 부족합니다."),

    COOKIE_NULL(HttpStatus.BAD_REQUEST, "AUTH_3", "Cookie가 존재하지 않습니다."),
    REFRESH_TOKEN_NULL(HttpStatus.BAD_REQUEST, "AUTH_4", "Refresh Token이 존재하지 않습니다."),
    TOKEN_CATEGORY_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH_5", "Token 카테고리가 Refresh와 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_6", "서버에 존재하지 않는 Refresh Token입니다."),
    ACCESS_TOKEN_NULL(HttpStatus.BAD_REQUEST, "AUTH_7", "Access Token이 존재하지 않습니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_0", "user 정보를 찾을 수 없음."),

    // S3 이미지 관련 에러
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_0", "이미지 업로드 중 오류가 발생했습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_1", "AWS S3 업로드에 실패했습니다."),
    FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_2", "이미지 파일을 읽는 중 오류가 발생했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_3", "AWS S3 이미지 삭제에 실패했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_4", "DB 이미지 삭제에 실패했습니다."),

    // 게시글 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post404_0", "게시글이 존재하지 않습니다."),
    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "Post403_0", "이 게시글을 수정할 권한이 없습니다."),
    UNAUTHORIZED_DELETE_ACTION(HttpStatus.FORBIDDEN, "Post403_1", "이 게시글을 삭제할 권한이 없습니다."),

    // 로그인/회원가입 에러
    MISSING_REQUIRED_FIELDS(HttpStatus.BAD_REQUEST, "LOGIN_0", "입력된 회원정보에 오류가 있습니다."),
    DATA_PERSISTENCE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "LOGIN_1", "데이터 저장 중 오류가 발생했습니다."),

    // 메인 기능 에러
    SHELTER_NOT_FOUND(HttpStatus.NOT_FOUND,"MAIN_0","등록된 보호소 정보를 찾을 수 없음."),

    //예약 관련
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST,"RES_0","방문 예약이 불가능한 시간으로 예약을 요청함."),
    SHELTER_NOT_REGISTERED(HttpStatus.BAD_REQUEST,"RES_1","방문 예약이 불가능한 보호소를 대상으로 예약을 요청함."),

    //서버 에러
    UNKNOWN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SERVER_0","처리하지 못한 서버 내부 error 발생");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ResponseCodeEnum(HttpStatus status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;

    }

}

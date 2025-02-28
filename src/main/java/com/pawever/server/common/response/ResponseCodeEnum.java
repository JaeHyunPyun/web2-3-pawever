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

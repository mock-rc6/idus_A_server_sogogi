package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users, /users/login
    POST_USERS_EMPTY_EMAIL(false, 2020, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2021, "이메일 형식을 확인해주세요."),
    POST_USERS_OVER_LENGTH_EMAIL(false, 2022, "이메일 길이를 확인해주세요."),


    POST_USERS_EMPTY_PHONENUMBER(false,2030,"휴대폰 번호를 입력해주세요."),
    POST_USERS_INVALID_PHONENUMBER(false,2031,"잘못된 휴대폰 번호입니다."),

    POST_USERS_EMPTY_NAME(false,2040,"이름을 입력해주세요."),
    POST_USERS_OVER_LENGTH_NAME(false,2041,"이름은 최대 20자까지 입력해주세요."),

    POST_USERS_EMPTY_PASSWORD(false,2050,"비밀 번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false,2051,"비밀 번호는 영문+숫자+특수문자 8자 이상입니다."),
    
    
    PATCH_USERS_OVERFLOW_URL(false,2060,"URL 최대길이를 넘어섰습니다."),

    PATCH_USERS_INVALID_BIRTHDAY(false,2070,"잘못된 생일 형식입니다."),

    PATCH_USERS_INVALID_GENDER(false,2080,"잘못된 성별 형식입니다."),




    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3010, "중복된 이메일입니다."),
    
    POST_USERS_EXISTS_PHONENUMBER(false,3020,"중복된 휴대폰 번호입니다."),

    FAILED_TO_LOGIN(false,3014,"없는 이메일이거나 비밀번호가 틀렸습니다."),

    FAILED_TO_SEARCH_PRODUCT(false,3030,"존재하지 않는 작품입니다."),

    FAILED_TO_SEARCH_ONLINE_CLASS(false,3040,"존재하지 않는 온라인 클래스입니다."),

    FAILED_TO_SEARCH_OFFLINE_CLASS(false,3050,"존재하지 않는 오프라인 클래스입니다."),
    
    FAILED_TO_SEARCH_ONLINE_CLASS_REVIEW(false,3060,"존재하지 않는 온라인 클래스 수강후기입니다."),
    
    FAILED_TO_SEARCH_OFFLINE_CLASS_REVIEW(false,3070,"존재하지 않는 오프라인 클래스 참여후기입니다."),

    FAILED_TO_SEARCH_PRODUCT_CATEGORY(false,3100,"존재하지 않는 작품 카테고리입니다."),
    FAILED_TO_SEARCH_OFFLINE_CATEGORY(false,3200,"존재하지 않는 오프라인 클래스 카테고리입니다."),
    FAILED_TO_SEARCH_OFFLINE_ADDRESS(false,3300,"존재하지 않는 오프라인 클래스 주소입니다."),
    
    
    DUPLICATED_PRODUCT_LIKE(false,3310,"중복되는 작품 찜입니다."),
    DUPLICATED_ONLINE_LIKE(false,3320,"중복되는 온라인 클래스 찜입니다."),
    DUPLICATED_OFFLINE_LIKE(false,3330,"중복되는 오프라인 클래스 찜입니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

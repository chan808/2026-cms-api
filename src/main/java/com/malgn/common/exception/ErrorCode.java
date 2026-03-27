package com.malgn.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),

    // auth
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // content
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}

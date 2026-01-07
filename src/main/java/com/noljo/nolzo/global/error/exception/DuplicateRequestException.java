package com.noljo.nolzo.global.error.exception;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException() {
        super("멱등성 체크 위반. 중복 요청입니다.");
    }

    public DuplicateRequestException(String message) {
        super(message);
    }
}

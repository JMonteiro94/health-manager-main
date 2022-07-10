package com.myhealth.healthmanagermain.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

@Slf4j
public class ApiException extends RuntimeException {

    private final int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        log.error("Exception thrown: code {} | msg: {}", code, msg);
    }

    public int getCode() {
        return code;
    }
}

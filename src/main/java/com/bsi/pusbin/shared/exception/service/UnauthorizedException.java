package com.bsi.pusbin.shared.exception.service;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

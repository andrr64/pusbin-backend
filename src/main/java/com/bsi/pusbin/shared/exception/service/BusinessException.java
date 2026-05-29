package com.bsi.pusbin.shared.exception.service;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class BusinessException extends AppException {
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

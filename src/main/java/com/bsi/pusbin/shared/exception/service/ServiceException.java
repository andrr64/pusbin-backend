package com.bsi.pusbin.shared.exception.service;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class ServiceException extends AppException {
    public ServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.bsi.pusbin.shared.exception.db;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

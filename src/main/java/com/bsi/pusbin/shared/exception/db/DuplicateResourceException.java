package com.bsi.pusbin.shared.exception.db;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

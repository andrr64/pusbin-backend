package com.bsi.pusbin.shared.exception.db;

import com.bsi.pusbin.shared.exception.AppException;
import org.springframework.http.HttpStatus;

public class DatabaseException extends AppException {
    public DatabaseException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

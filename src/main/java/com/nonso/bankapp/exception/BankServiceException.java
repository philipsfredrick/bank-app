package com.nonso.bankapp.exception;

import com.nonso.bankapp.dto.ErrorCode;
import org.springframework.http.HttpStatus;

public class BankServiceException extends BankAppException {
    public BankServiceException(String msg) {
        super(msg);
    }

    public BankServiceException(String msg, HttpStatus status) {
        super(msg, status);
    }

    public BankServiceException(String msg, HttpStatus status, ErrorCode errorCode) {
        super(msg, status, errorCode);
    }
}

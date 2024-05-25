package com.nonso.bankapp.exception;

import com.nonso.bankapp.dto.ErrorCode;
import org.springframework.http.HttpStatus;

public class AccountServiceException extends BankAppException {

    public AccountServiceException(String msg) {
        super(msg);
    }

    public AccountServiceException(String msg, HttpStatus status) {
        super(msg,status);
    }

    public AccountServiceException(String msg, HttpStatus status, ErrorCode errorCode) {
        super(msg, status, errorCode);
    }
}

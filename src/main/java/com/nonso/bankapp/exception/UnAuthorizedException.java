package com.nonso.bankapp.exception;

import com.nonso.bankapp.dto.ErrorCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class UnAuthorizedException extends BankAppException {

    @Serial
    private static final long serialVersionUID = -7842817615484430014L;

    public UnAuthorizedException(String msg) {
        super(msg, UNAUTHORIZED);
    }

    public UnAuthorizedException(String msg, ErrorCode errorCode) {
        super(msg, UNAUTHORIZED, errorCode);
    }
}

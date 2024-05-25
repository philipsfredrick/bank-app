package com.nonso.bankapp.exception;

import com.nonso.bankapp.dto.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@NoArgsConstructor
public class BankAppException extends RuntimeException {

    protected HttpStatus status;
    private ErrorCode errorCode;
    private String metadata;
    private String infoLink;

    public BankAppException(final String msg) {
        super(msg);
    }

    public BankAppException(final String msg, final HttpStatus status) {
        this(msg);
        this.status = status;
    }

    public BankAppException(final String msg, final HttpStatus status, final ErrorCode errorCode) {
        this(msg, status);
        this.errorCode = errorCode;
    }

    public BankAppException(final String msg, final HttpStatus status, final String metadata) {
        this(msg, status);
        this.metadata = metadata;
    }

    public BankAppException(final String msg, final HttpStatus status, final ErrorCode errorCode, final String metadata) {
        this(msg,status, errorCode);
        this.metadata = metadata;
    }
}

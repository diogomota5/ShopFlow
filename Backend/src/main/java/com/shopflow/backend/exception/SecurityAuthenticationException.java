package com.shopflow.backend.exception;

import java.io.Serial;

public class SecurityAuthenticationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SecurityAuthenticationException(String message) {
        super(message);
    }
}

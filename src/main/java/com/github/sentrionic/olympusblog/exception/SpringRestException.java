package com.github.sentrionic.olympusblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpringRestException extends RuntimeException {
    public SpringRestException(String message) {}
}

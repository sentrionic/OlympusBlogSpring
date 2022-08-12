package com.github.sentrionic.olympusblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The cursor is not a valid date")
public class InvalidCursorException extends RuntimeException {
}

package com.github.sentrionic.olympusblog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Something went wrong. Try again later")
public class FileUploadException extends RuntimeException {
}

package com.sulwork.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(Instant timestamp, int status, String message, String path, Map<String, String> fields) {
    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(Instant.now(), status, message, path, null);
    }
}
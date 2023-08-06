package com.engine.errors.response;

import com.engine.errors.CommonResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorControllerAdvice {

    private ResponseEntity<CommonResponse.ApiResult<?>> newResponse(String message, HttpStatus status) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(CommonResponse.error(message, status), headers, status);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        System.out.println(e.getMessage());
        return newResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

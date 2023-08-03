package com.arms.errors.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.arms.errors.CommonResponse.ApiResult;
import static com.arms.errors.CommonResponse.error;

@ControllerAdvice
public class ErrorControllerAdvice {

    private ResponseEntity<ApiResult<?>> newResponse(String message, HttpStatus status) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(error(message, status), headers, status);
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

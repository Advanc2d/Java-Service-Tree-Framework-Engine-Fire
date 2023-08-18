package com.arms.errors.response;

import com.arms.errors.CommonResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /*
    * connectId 값이 정상적으로 넘어오지 않았을 때 오류 처리
    * */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgumentException(MissingPathVariableException ex) {

        String variableName = ex.getVariableName();
        if("connectId".equals(variableName)){
            return newResponse("connectId 데이터가 수신되지 않았습니다.",HttpStatus.BAD_REQUEST);
        }
        return newResponse("정상적으로 데이터가 수신되지 않았습니다.",HttpStatus.BAD_REQUEST);
    }
}

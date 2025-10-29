package com.rejs.reservation.global.controller.advice;

import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.exception.GlobalBaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(value = GlobalBaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(GlobalBaseException ex){
       return new ResponseEntity<>(BaseResponse.exception(ex.getStatus()), ex.getStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(RuntimeException ex){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(BaseResponse.exception(status), status);
    }

}

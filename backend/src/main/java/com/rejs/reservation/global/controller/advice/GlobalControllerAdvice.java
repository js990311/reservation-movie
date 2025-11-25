package com.rejs.reservation.global.controller.advice;

import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.dto.response.BusinessExceptionResponse;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.GlobalBaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<BusinessExceptionResponse> handleBusinessException(BusinessException ex, HttpServletRequest request){
        return ResponseEntity.status(ex.getCode().getStatus()).body(new BusinessExceptionResponse(ex.getCode(), request.getRequestURI(), ex.getDetail()));    }

    @Deprecated
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

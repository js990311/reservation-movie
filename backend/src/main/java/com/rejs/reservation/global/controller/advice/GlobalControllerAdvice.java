package com.rejs.reservation.global.controller.advice;

import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.dto.response.BusinessExceptionResponse;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import com.rejs.reservation.global.exception.code.InternalServerExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(BusinessException ex, HttpServletRequest request){
        return ResponseEntity.status(ex.getCode().getStatus()).body(BusinessExceptionResponse.of(ex.getCode(), request.getRequestURI(), ex.getDetail()));    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(RuntimeException ex, HttpServletRequest request){
        BusinessExceptionCode code = InternalServerExceptionCode.INTERNAL_SERVER_ERROR;
        String instance = request.getRequestURI();
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(instance).append("] ").append(ex.getMessage());
        log.warn(sb.toString());
        return ResponseEntity.status(code.getStatus()).body(BusinessExceptionResponse.of(code, instance));
    }

}

package com.rejs.reservation.global.controller.advice;

import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.dto.response.BusinessExceptionResponse;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import com.rejs.reservation.global.exception.code.InternalServerExceptionCode;
import com.rejs.reservation.global.security.exception.AuthenticationExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BusinessExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        AuthenticationExceptionCode code = AuthenticationExceptionCode.INVALID_PATH;
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new BusinessExceptionResponse(code, request.getRequestURI(), "존재하지 않는 API 경로입니다."));
    }
}

package com.rejs.reservation.global.trace.aspect;

import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class ControllerLogAspect {
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object controllerLogging(ProceedingJoinPoint joinPoint) throws Throwable{
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[REQUEST] {}({})", methodName, argsToString(args));
        try {
            return joinPoint.proceed();
        }finally {
            log.info("[RESPONSE] {}", methodName);
        }
    }

    public String argsToString(Object[] args){
        if(args == null || args.length == 0){
            return "none";
        }

        return Arrays.stream(args)
                .filter(arg->{
                    if(arg instanceof HttpServletRequest
                            || arg instanceof HttpSession
                            || arg instanceof ClaimsDto
                    ){
                        return false;
                    }else {
                        return true;
                    }
                })
                .map(arg -> {
                    if(arg instanceof MultipartFile){
                        return "MultipartFile";
                    }else if(arg instanceof LoginRequest){
                        return "LOGIN REQUEST MASKED";
                    }else if(arg instanceof Pageable pageable){
                        return String.format("page: %d, size: %d",pageable.getPageNumber(), pageable.getPageSize());
                    }else if(arg == null){
                        return "NULL";
                    }
                    String str = arg.toString();
                    return str.length() > 500 ? str.substring(0,500) + "..." : str;
                }).collect(Collectors.joining(", "));
    }
}

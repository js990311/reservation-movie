package com.rejs.reservation.global.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseResponse<T> {
    private final int status;
    private final T data;

    public BaseResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> BaseResponse<T> of(int status,T data){
        return new BaseResponse<>(status, data);
    }

    public static <T> BaseResponse<T> exception(HttpStatus status){
        return new BaseResponse<>(status.value(), null);
    }
}

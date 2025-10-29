package com.rejs.reservation.global.dto.response;

import lombok.Getter;

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
}

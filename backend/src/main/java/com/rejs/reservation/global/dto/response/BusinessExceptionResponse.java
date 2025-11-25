package com.rejs.reservation.global.dto.response;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.Getter;

@Getter
public class BusinessExceptionResponse {
    private String type;
    private String title;
    private Integer status;
    private String instance;
    private String detail;

    public BusinessExceptionResponse(BusinessExceptionCode code, String instance, String detail) {
        this.type = code.getType();
        this.title = code.getTitle();
        this.status = code.getStatus().value();
        this.instance = instance;
        if(detail == null){
            this.detail = code.getTitle();
        }else {
            this.detail = detail;
        }
    }

}

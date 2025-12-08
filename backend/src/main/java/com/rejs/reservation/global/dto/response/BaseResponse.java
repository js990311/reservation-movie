package com.rejs.reservation.global.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rejs.reservation.global.dto.response.metadata.CollectionMetadata;
import com.rejs.reservation.global.dto.response.metadata.PageMetadata;
import com.rejs.reservation.global.dto.response.metadata.SliceMetadata;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private final T data;
    private final CollectionMetadata pagination;
    private final BusinessExceptionResponse error;

    public BaseResponse(T data) {
        this.data = data;
        this.pagination = null;
        this.error = null;
    }

    public BaseResponse(BusinessExceptionResponse error) {
        this.data = null;
        this.pagination = null;
        this.error = error;
    }

    public BaseResponse(T data, CollectionMetadata pagination) {
        this.data = data;
        this.pagination = pagination;
        this.error = null;
    }

    public static <T> BaseResponse<T> of(T data){
        return new BaseResponse<>(data);
    }

    public static <E> BaseResponse<List<E>> ofList(List<E> data){
        return new BaseResponse<>(data, CollectionMetadata.of(data));
    }

    public static <E> BaseResponse<List<E>> ofSlice(Slice<E> data){
        return new BaseResponse<>(data.getContent(), SliceMetadata.of(data));
    }

    public static <E> BaseResponse<List<E>> ofPage(Page<E> data){
        return new BaseResponse<>(data.getContent(), PageMetadata.of(data));
    }

    public static BaseResponse<?> fail(BusinessExceptionResponse error){
        return new BaseResponse<>(error);
    }
}

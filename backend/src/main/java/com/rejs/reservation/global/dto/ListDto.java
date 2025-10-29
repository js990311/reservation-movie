package com.rejs.reservation.global.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ListDto<T> {
    private Integer count;
    private List<T> elements;

    public ListDto(List<T> elements) {
        this.count = elements.size();
        this.elements = elements;
    }

    public static <T> ListDto<T> from(List<T> elements){
        return new ListDto<>(elements);
    }
}

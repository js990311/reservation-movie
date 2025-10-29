package com.rejs.reservation.domain.theater.dto.request;

import lombok.Getter;

@Getter
public class TheaterCreateRequest {
    private String name;
    private Integer rowSize;
    private Integer colSize;
}

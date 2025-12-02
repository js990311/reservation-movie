package com.rejs.reservation.domain.theater.dto;

import com.rejs.reservation.domain.theater.entity.Theater;
import lombok.Getter;

import java.util.List;

@Getter
public class TheaterSummaryDto {
    private Long theaterId;
    private String name;
    private Integer rowSize;
    private Integer colSize;

    public TheaterSummaryDto(Long theaterId, String name, Integer rowSize, Integer colSize) {
        this.theaterId = theaterId;
        this.name = name;
        this.rowSize = rowSize;
        this.colSize = colSize;
    }

    public static TheaterSummaryDto from(Theater theater){
        return new TheaterSummaryDto(
                theater.getId(),
                theater.getName(),
                theater.getRowSize(),
                theater.getColSize()
        );
    }

}

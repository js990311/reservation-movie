package com.rejs.reservation.domain.theater.dto;

import com.rejs.reservation.domain.theater.entity.Theater;
import lombok.Getter;

import java.util.List;

@Getter
public class TheaterSummaryDto {
    private Long theaterId;
    private String name;

    public TheaterSummaryDto(Long theaterId, String name) {
        this.theaterId = theaterId;
        this.name = name;
    }

    public static TheaterSummaryDto from(Theater theater){
        return new TheaterSummaryDto(
                theater.getId(),
                theater.getName()
        );
    }

}

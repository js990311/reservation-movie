package com.rejs.reservation.domain.screening.dto;

import com.rejs.reservation.domain.screening.entity.Screening;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ScreeningDto {
    private Long screeningId;
    private Long theaterId;
    private Long movieId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ScreeningDto from(Screening screening){
        return ScreeningDto.builder()
                .screeningId(screening.getId())
                .theaterId(screening.getTheater().getId())
                .movieId(screening.getMovie().getId())
                .startTime(screening.getStartTime())
                .endTime(screening.getEndTime())
                .build();
    }
}

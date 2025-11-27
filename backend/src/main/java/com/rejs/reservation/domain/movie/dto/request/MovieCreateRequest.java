package com.rejs.reservation.domain.movie.dto.request;

import lombok.Getter;

@Getter
public class MovieCreateRequest {
    private String title;
    private Integer duration;

    public MovieCreateRequest(String title, Integer duration) {
        this.title = title;
        this.duration = duration;
    }
}

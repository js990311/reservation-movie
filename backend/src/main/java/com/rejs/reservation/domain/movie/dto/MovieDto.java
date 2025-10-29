package com.rejs.reservation.domain.movie.dto;

import com.rejs.reservation.domain.movie.entity.Movie;
import lombok.Getter;

@Getter
public class MovieDto {
    private Long movieId;
    private String title;
    private Integer duration;

    public MovieDto(Long movieId, String title, Integer duration) {
        this.movieId = movieId;
        this.title = title;
        this.duration = duration;
    }

    public static MovieDto from(Movie movie){
        return new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDuration()
        );
    }
}

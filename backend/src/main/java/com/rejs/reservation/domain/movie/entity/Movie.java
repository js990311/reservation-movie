package com.rejs.reservation.domain.movie.entity;

import com.rejs.reservation.domain.screening.entity.Screening;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue
    @Column(name = "movie_id")
    private Long id;

    @Column
    private String title;

    @Column
    private Integer duration;

    /* 관계 - screening */
    @Builder.Default
    @OneToMany(mappedBy = "movie")
    private List<Screening> screenings = new ArrayList<>();

    public void addScreening(Screening screening){
        this.screenings.add(screening);
        screening.mapMovie(this);
    }

    public void removeScreening(Screening screening){
        screenings.remove(screening);
        screening.mapMovie(null);
    }
}

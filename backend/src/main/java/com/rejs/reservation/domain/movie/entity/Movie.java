package com.rejs.reservation.domain.movie.entity;

import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE movies SET deleted_at = NOW() WHERE movie_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "movies")
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

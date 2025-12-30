package com.rejs.reservation.domain.screening.entity;

import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE screenings SET deleted_at = NOW() WHERE screening_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "screenings")
public class Screening extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_id")
    private Long id;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    /* # 관계 - Movie */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "movie_id", insertable = false, updatable = false)
    private Long movieId;

    public Long getMovieId(){
        return movie != null ? movie.getId() : null;
    }

    /**
     * @deprecated 의존관계 매핑을 위한 헬퍼 메서드에서만 사용하십시오. {@link #assignMovie(Movie)}를 대신 사용하십시오
     */
    @Deprecated
    public void mapMovie(Movie movie){
        this.movie = movie;
    }

    public void assignMovie(Movie movie){
        if(this.movie != null){
            this.movie.removeScreening(this);
        }
        if(movie != null){
            movie.addScreening(this);
        }
    }

    // # 관계 - Theater
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @Column(name = "theater_id", updatable = false, insertable = false)
    private Long theaterId;

    public Long getTheaterId(){
        return theater != null ? theater.getId() : null;
    }

    @Deprecated
    public void mapTheater(Theater theater){
        this.theater = theater;
    }

    public void assignTheater(Theater theater){
        if(this.theater != null){
            this.theater.removeScreenings(this);
        }
        if(theater != null){
            theater.addScreenings(this);
        }
    }

    // 로직
    public void updateScreeningTime(LocalDateTime startTime){
        this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
        this.endTime = startTime.plusMinutes(movie.getDuration());
    }

    // 생성
    public static Screening of(LocalDateTime startTime, Theater theater, Movie movie){
        Screening screening = new Screening();
        screening.assignTheater(theater);
        screening.assignMovie(movie);
        screening.updateScreeningTime(startTime);
        return screening;
    }
}

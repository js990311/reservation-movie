package com.rejs.reservation.domain.screening.entity;

import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "screenings")
public class Screening {
    @Id
    @GeneratedValue
    @Column(name = "screening_id")
    private Long id;

    /* # 관계 - Screening */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

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

    /* # 관계 - Reservation */

    @OneToMany(mappedBy = "screening")
    private List<Reservation> reservations = new ArrayList<>();


    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
        reservation.mapScreening(this);
    }

    public void removeScreening(Reservation reservation){
        reservations.remove(reservation);
        reservation.mapScreening(null);
    }

}

package com.rejs.reservation.domain.screening.entity;

import com.rejs.reservation.domain.theater.entity.Seat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScreeningSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screening_seat_id")
    private Long id;

    @Column
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id")
    private Screening screening;

    @Enumerated(EnumType.STRING)
    @Column
    private ScreeningSeatStatus status;

    public ScreeningSeat(Screening screening, Seat seat) {
        this.seat = seat;
        this.screening = screening;
        this.price = 10000;
        this.status = ScreeningSeatStatus.AVAILABLE;
    }
}

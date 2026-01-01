package com.rejs.reservation.domain.screening.entity;

import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "screening_seats")
@SQLDelete(sql = "UPDATE screeing_seats SET deleted_at = NOW() WHERE screeing_seat_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ScreeningSeat extends BaseEntity {
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

    public void reserved() {
        this.status = ScreeningSeatStatus.RESERVED;
    }

    public void available(){
        this.status = ScreeningSeatStatus.AVAILABLE;
    }
}

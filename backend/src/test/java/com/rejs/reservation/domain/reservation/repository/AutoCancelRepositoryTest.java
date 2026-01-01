package com.rejs.reservation.domain.reservation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@Import({QueryDslConfig.class, TestcontainersConfiguration.class})
class AutoCancelRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private AutoCancelRepository autoCancelRepository;

    User user;
    Theater theater;
    Movie movie;
    Screening screening;

    @BeforeEach
    void setup() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        autoCancelRepository = new AutoCancelRepository(queryFactory, reservationRepository);

        // user -> reservation
        user = new User("mock", "mock");
        user = userRepository.save(user);

        // theater, movie -> screening -> reservation
        theater = new Theater("mock",3,3);
        theaterRepository.save(theater);

        movie = Movie.builder().title("mock").duration(3).build();
        movie = movieRepository.save(movie);

        screening = Screening.create(LocalDateTime.now().minusDays(1), theater, movie);
        screening = screeningRepository.save(screening);


    }

    @Test
    @DisplayName("생성 10분 초과 PENDING 예약 취소: 자식 엔티티(ReservationSeat)와의 FK 정합성을 유지하며 수행된다.")
    void autoCancelByCreatedAt_WithFK() {
        Reservation oldPending = new Reservation(user.getId(), screening.getId(), 10);
        oldPending = reservationRepository.save(oldPending);

        int touched = em.createNativeQuery(
                        "update reservations set created_at = :t where reservation_id = :id"
                )
                .setParameter("t", LocalDateTime.now().minusMinutes(30))
                .setParameter("id", oldPending.getId())
                .executeUpdate();
        Reservation recentPending = reservationRepository.save(new Reservation(user.getId(), screening.getId(), 10));

        em.flush();
        em.clear();

        long updated = autoCancelRepository.autoCancelByCreatedAt();

        em.flush();
        em.clear();

        assertEquals(1L, updated);
    }

    @Test
    @DisplayName("상영 시간 경과 PENDING 예약 취소: Screening 연관 관계를 타고 정상 취소된다.")
    void autoCancelByScreeningStartTime_WithFK() {
        Screening past = screeningRepository.save(Screening.create(LocalDateTime.now().minusDays(1), theater, movie));
        Screening future = screeningRepository.save(Screening.create(LocalDateTime.now().plusDays(1), theater, movie));

        Reservation futurePending = reservationRepository.save(new Reservation(user.getId(), future.getId(), 10));
        Reservation pastPending = reservationRepository.save(new Reservation(user.getId(), past.getId(), 10));

        em.flush();
        em.clear();

        long updated = autoCancelRepository.autoCancelByScreeningStartTime();

        em.flush();
        em.clear();

        assertEquals(1, updated);
        assertThat(reservationRepository.findById(pastPending.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.CANCELED);
        assertThat(reservationRepository.findById(futurePending.getId()).orElseThrow().getStatus())
                .isEqualTo(ReservationStatus.PENDING);
    }
}
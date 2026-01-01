package com.rejs.reservation.domain.reservation.repository;

import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.domain.screening.repository.ScreeningSeatRepository;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.SeatDto;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@SpringBootTest
class ReservationQueryRepositoryTest {
    @Autowired
    private MovieService movieService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationQueryRepository reservationQueryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScreeningSeatRepository screeningSeatRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MovieDto movie;
    private TheaterDto theater;
    private ScreeningDto screening;
    private UserDto user;

    @BeforeEach
    void setup() {
        // 영화 생성
        MovieCreateRequest movieRequest = new MovieCreateRequest("title", 145);
        movie = movieService.createMovie(movieRequest);

        // 영화관 생성
        TheaterCreateRequest theaterRequest = new TheaterCreateRequest("theater1", 30, 30);
        theater = theaterService.createTheater(theaterRequest);

        // 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theater.getTheaterId(), movie.getMovieId(), LocalDateTime.now());
        screening = screeningService.createScreening(screeningRequest);

        User usere = new User("username", "password");
        usere = userRepository.save(usere);
        user = UserDto.of(usere);
    }

    @Test
    void 예약된좌석없음() {
        List<Long> seats = screeningSeatRepository.findByScreeningId(screening.getScreeningId()).stream().map(ScreeningSeat::getId).toList();
        ReservationRequest reservationRequest = new ReservationRequest(screening.getScreeningId(), seats);

        List<ScreeningSeat> availableSeats = reservationQueryRepository.selectAvailableSeats(
                reservationRequest.getSeats()
        );

        assertEquals(seats.size(), availableSeats.size());
    }

    @Test
    void 일부좌석이예약() {
        // 예약이 진행되었다고 가정
        int reservationSeatCount = 5;
        List<ScreeningSeat> seats = screeningSeatRepository.findByScreeningId(screening.getScreeningId());
        Reservation reservation = Reservation.create(user.getUserId(), screening.getScreeningId(), seats.subList(0, reservationSeatCount));
        reservationRepository.save(reservation);

        entityManager.flush();

        //
        List<ScreeningSeat> availableSeats = reservationQueryRepository.selectAvailableSeats(
                seats.stream().map(ScreeningSeat::getId).toList()
        );
        assertEquals(seats.size() - reservationSeatCount, availableSeats.size());
    }

    @Test
    void 모든좌석예약() {
        // 예약이 진행되었다고 가정
        List<ScreeningSeat> seats = screeningSeatRepository.findByScreeningId(screening.getScreeningId());
        Reservation reservation = Reservation.create(user.getUserId(), screening.getScreeningId(), seats);
        reservationRepository.save(reservation);

        entityManager.flush();

        //
        List<ScreeningSeat> availableSeats = reservationQueryRepository.selectAvailableSeats(
                seats.stream().map(ScreeningSeat::getId).toList()
        );
        assertEquals(0, availableSeats.size());
    }

    @Test
    void 다른상영시간의영향을받는지() {
        // 다른 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theater.getTheaterId(), movie.getMovieId(), LocalDateTime.now().plus(999, ChronoUnit.MINUTES));
        ScreeningDto screening2 = screeningService.createScreening(screeningRequest);

        // 기존 상영표의 예약이 모두 완료되었다고 가정
        List<ScreeningSeat> seats = screeningSeatRepository.findByScreeningId(screening.getScreeningId());
        Reservation reservation = Reservation.create(user.getUserId(), screening.getScreeningId(), seats);
        reservationRepository.save(reservation);

        entityManager.flush();

        seats = screeningSeatRepository.findByScreeningId(screening2.getScreeningId());
        // 다른 상영표는 모두 예매 가능
        List<ScreeningSeat> availableSeats = reservationQueryRepository.selectAvailableSeats(
                seats.stream().map(ScreeningSeat::getId).toList()
        );
        assertEquals(seats.size(), availableSeats.size());
    }
}
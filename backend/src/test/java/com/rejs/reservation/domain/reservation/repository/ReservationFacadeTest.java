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
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.SeatDto;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.entity.Seat;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@SpringBootTest
class ReservationFacadeTest {
    @Autowired
    private MovieService movieService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EntityManager entityManager;

    private MovieDto movie;
    private TheaterDto theater;
    private ScreeningDto screening;
    private UserDto user;

    @BeforeEach
    void setup(){
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
        List<SeatDto> seats = theater.getSeats();
        ReservationRequest reservationRequest = new ReservationRequest(screening.getScreeningId(), seats.stream().map(SeatDto::getSeatId).toList());

        List<Long> availableSeats = reservationFacade.selectAvailableSeats(
                reservationRequest.getSeats(),
                screening.getTheaterId(),
                screening.getScreeningId()
        );

        assertEquals(seats.size(), availableSeats.size());
    }

    @Test
    void 일부좌석이예약(){
        // 예약이 진행되었다고 가정
        int reservationSeatCount = 5;
        List<SeatDto> seats = theater.getSeats();
        Reservation reservation = Reservation.create(user.getUserId(), screening.getScreeningId(), seats.subList(0, reservationSeatCount).stream().map(SeatDto::getSeatId).toList());
        reservationRepository.save(reservation);

        entityManager.flush();

        //
        List<Long> availableSeats = reservationFacade.selectAvailableSeats(
                seats.stream().map(SeatDto::getSeatId).toList(),
                screening.getTheaterId(),
                screening.getScreeningId()
        );
        assertEquals(seats.size() - reservationSeatCount, availableSeats.size());
    }

    @Test
    void 모든좌석예약(){
        // 예약이 진행되었다고 가정
        List<SeatDto> seats = theater.getSeats();
        Reservation reservation = Reservation.create(user.getUserId(), screening.getScreeningId(), seats.stream().map(SeatDto::getSeatId).toList());
        reservationRepository.save(reservation);

        entityManager.flush();

        //
        List<Long> availableSeats = reservationFacade.selectAvailableSeats(
                seats.stream().map(SeatDto::getSeatId).toList(),
                screening.getTheaterId(),
                screening.getScreeningId()
        );
        assertEquals(0, availableSeats.size());
    }

    @Test
    void 다른상영시간의영향을받는지(){
        // 다른 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theater.getTheaterId(), movie.getMovieId(), LocalDateTime.now().plus(999, ChronoUnit.MINUTES));
        ScreeningDto screening2 = screeningService.createScreening(screeningRequest);

        // 기존 상영표의 예약이 모두 완료되었다고 가정
        List<SeatDto> seats = theater.getSeats();
        Reservation reservation = Reservation.create(user.getUserId(), screening.getScreeningId(), seats.stream().map(SeatDto::getSeatId).toList());
        reservationRepository.save(reservation);

        entityManager.flush();

        // 다른 상영표는 모두 예매 가능
        List<Long> availableSeats = reservationFacade.selectAvailableSeats(
                seats.stream().map(SeatDto::getSeatId).toList(),
                screening2.getTheaterId(),
                screening2.getScreeningId()
        );
        assertEquals(seats.size(), availableSeats.size());

    }
}
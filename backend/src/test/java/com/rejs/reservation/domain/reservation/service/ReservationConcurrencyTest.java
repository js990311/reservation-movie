package com.rejs.reservation.domain.reservation.service;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;
import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.facade.ReservationFacade;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.SeatDto;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.exception.BusinessException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ReservationConcurrencyTest {
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

    private MovieDto movie;
    private TheaterDto theater;
    private ScreeningDto screening;

    private int threadCount = 100;

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;
    private List<Long> users = new ArrayList<>();
    private ReservationRequest request;
    private AtomicInteger successCount;
    private AtomicInteger failCount;
    private AtomicInteger exceptionCount;

    @BeforeEach
    void setup(){
        // 영화 생성
        MovieCreateRequest movieRequest = new MovieCreateRequest("title", 145);
        movie = movieService.createMovie(movieRequest);

        // 영화관 생성
        TheaterCreateRequest theaterRequest = new TheaterCreateRequest("theater1", 3, 3);
        theater = theaterService.createTheater(theaterRequest);

        // 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theater.getTheaterId(), movie.getMovieId(), LocalDateTime.now());
        screening = screeningService.createScreening(screeningRequest);


        // 사용자 생성
        for(int i=0;i<threadCount;i++){
            User user = new User(TsidCreator.getTsid().toString(), "password");
            user = userRepository.save(user);
            users.add(user.getId());
        }

        request = new ReservationRequest(screening.getScreeningId(), theater.getSeats().stream().map(SeatDto::getSeatId).toList());

        executorService = Executors.newFixedThreadPool(threadCount);
        countDownLatch = new CountDownLatch(threadCount);
        successCount = new AtomicInteger();
        failCount = new AtomicInteger();
        exceptionCount = new AtomicInteger();
    }

    @Test
    void reservation() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        for (int i=0; i< threadCount; i++){
            int finalI = i;
            executorService.submit(()->{
                try {
                    barrier.await();
                    ReservationRequest request = new ReservationRequest(screening.getScreeningId(), List.of(theater.getSeats().get(finalI % theater.getSeats().size()).getSeatId()));
                    reservationFacade.reservationScreening(request, users.get(finalI));
                    successCount.incrementAndGet();
                }catch (BusinessException e){
                    failCount.incrementAndGet();
                }catch (Exception e){
                    exceptionCount.incrementAndGet();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertEquals(theater.getSeats().size(), successCount.get());
        assertEquals(threadCount - theater.getSeats().size(), failCount.get());
        assertEquals(0, exceptionCount.get());
    }

    @Test
    void reservationCheckDeadLock() throws InterruptedException {
        // 지금은 데드락이 안걸린다. screening에 락 하나만 잡기 때문에
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        int seatCount = theater.getSeats().size();
        for (int i=0; i< threadCount; i++){
            int finalI = i;
            executorService.submit(()->{
                try {
                    int base = finalI % seatCount;
                    int prev = (base - 1 + seatCount) % seatCount;
                    int next = (base + 1) % seatCount;
                    Long sPrev = theater.getSeats().get(prev).getSeatId();
                    Long sBase = theater.getSeats().get(base).getSeatId();
                    Long sNext = theater.getSeats().get(next).getSeatId();

                    List<Long> seats;
                    int mode = finalI % 3;
                    if (mode == 0) {
                        seats = List.of(sPrev, sBase, sNext);
                    } else if (mode == 1) {
                        seats = List.of(sNext, sBase, sPrev);
                    } else {
                        seats = List.of(sBase, sPrev, sNext);
                    }

                    barrier.await();
                    ReservationRequest request = new ReservationRequest(screening.getScreeningId(), seats);
                    reservationFacade.reservationScreening(request, users.get(finalI));
                    successCount.incrementAndGet();
                }catch (BusinessException e){
                    failCount.incrementAndGet();
                }catch (Exception e){
                    exceptionCount.incrementAndGet();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertTrue(successCount.get() <= 3);
        assertEquals(0, exceptionCount.get());
    }



}
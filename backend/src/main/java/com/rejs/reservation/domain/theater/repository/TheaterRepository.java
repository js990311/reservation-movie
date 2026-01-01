package com.rejs.reservation.domain.theater.repository;

import com.rejs.reservation.domain.theater.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
    @Query("select t from Theater t join fetch t.seats where t.id = :id")
    Optional<Theater> findWithSeatsById(@Param("id") Long id);
}

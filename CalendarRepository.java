package com.example.finalproject.repository;

import com.example.finalproject.entity.Calendar;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends CrudRepository<Calendar, Long> {

    @Override
    List<Calendar> findAll();  // 전체 조회

    // 특정 날짜로 조회 (엔티티에 해당 필드가 있어야 함)
    Optional<Calendar> findByDate(LocalDate date);
    List<Calendar> findByDateBetween(LocalDate start, LocalDate end);
}

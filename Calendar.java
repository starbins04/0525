package com.example.finalproject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@ToString
@Slf4j
@NoArgsConstructor
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mini;         // 시급 (원)
    private LocalDate date;    // 근무 날짜
    private LocalTime startTime; // 출근 시간
    private LocalTime endTime;   // 퇴근 시간
    private Integer breakTime; // 휴게 시간 (분)

    public Calendar(Long mini, LocalDate date, LocalTime startTime, LocalTime endTime, Integer breakTime) {
        this.mini = mini;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakTime = breakTime;
    }

    public Calendar(LocalDate date) {
        this.date = date;
    }

}

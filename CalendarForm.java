package com.example.finalproject.dto;

import com.example.finalproject.entity.Calendar;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Slf4j
public class CalendarForm {
    private Long mini;         // 시급 (원)
    private LocalDate date;    // 근무 날짜
    private LocalTime startTime; // 출근 시간
    private LocalTime endTime;   // 퇴근 시간
    private Integer breakTime; // 휴게 시간 (분)

    public Calendar toEntity() { return new Calendar(mini, date, startTime, endTime, breakTime); }
}

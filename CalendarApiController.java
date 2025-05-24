package com.example.finalproject.api;

import com.example.finalproject.dto.CalendarForm;
import com.example.finalproject.entity.Calendar;
import com.example.finalproject.repository.CalendarRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarApiController {

    private final CalendarRepository calendarRepository;

    public CalendarApiController(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    // 전체 조회
    @GetMapping
    public List<Calendar> list() {
        return calendarRepository.findAll();
    }

    // 단일 조회 (id 기준)
    @GetMapping("/{id}")
    public Calendar read(@PathVariable("id") Long id) {
        return calendarRepository.findById(id).orElse(null);
    }

    // 생성
    @PostMapping
    public Calendar create(@RequestBody CalendarForm calendarForm) {
        Calendar calendar = calendarForm.toEntity();
        return calendarRepository.save(calendar);
    }

}

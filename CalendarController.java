package com.example.finalproject.controller;

import com.example.finalproject.entity.Calendar;
import com.example.finalproject.dto.CalendarForm;
import com.example.finalproject.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.*;
import java.util.*;

@Controller
public class CalendarController {
    @Autowired
    private CalendarRepository calendarRepository;

    // 메인 캘린더 페이지 (이번 달 총 월급 포함)
    @GetMapping("/calendar")
    public String calendarPage(Model model, @ModelAttribute("msg") String msg) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Calendar> monthlyCalendars = calendarRepository.findByDateBetween(startOfMonth, endOfMonth);

        long totalWage = 0;
        for (Calendar cal : monthlyCalendars) {
            long workMinutes = Duration.between(cal.getStartTime(), cal.getEndTime()).toMinutes() - cal.getBreakTime();
            long workedHours = workMinutes / 60;
            totalWage += workedHours * cal.getMini();
        }

        model.addAttribute("totalWage", totalWage);
        model.addAttribute("msg", msg);
        return "calendar";
    }

    @GetMapping("/calendar/new")
    public String newCalendarForm() {
        return "new"; // 등록 폼 (날짜 없음)
    }

    @GetMapping("/calendar/new/{date}")
    public String newCalendarFormWithDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        Calendar calendar = new Calendar();
        calendar.setDate(date);
        model.addAttribute("calendar", calendar);
        return "new"; // 날짜가 채워진 등록 폼
    }

    @PostMapping("/calendar/create")
    public String createCalendar(CalendarForm form) {
        Calendar calendar = form.toEntity();
        Calendar saved = calendarRepository.save(calendar);
        return "redirect:/calendar/" + saved.getDate(); // 예: 2025-05-18
    }

    @GetMapping("/calendar/{date}")
    public String showCalendar(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model, RedirectAttributes redirectAttributes) {
        Optional<Calendar> optionalCalendar = calendarRepository.findByDate(date);

        if (optionalCalendar.isPresent()) {
            model.addAttribute("calendar", optionalCalendar.get());
            return "calendars/show";
        } else {
            // 날짜만 채워서 new 폼으로 넘기기
            redirectAttributes.addFlashAttribute("calendar", new Calendar(date));
            return "redirect:/calendar/new?date=" + date.toString();
        }
    }

    // 전체 이벤트 (캘린더용 JSON 반환)
    @GetMapping("/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents() {
        List<Calendar> calendars = calendarRepository.findAll();
        List<Map<String, Object>> events = new ArrayList<>();

        for (Calendar cal : calendars) {
            LocalTime start = cal.getStartTime();
            LocalTime end = cal.getEndTime();
            long workMinutes = Duration.between(start, end).toMinutes() - cal.getBreakTime();

            long hours = workMinutes / 60;
            long minutes = workMinutes % 60;

            String displayTime = hours + "시간" + (minutes > 0 ? " " + minutes + "분" : "");
            Map<String, Object> event = new HashMap<>();
            event.put("title", "⏰ " + displayTime);
            event.put("start", cal.getDate().toString());
            event.put("allDay", true);
            events.add(event);
        }

        return events;
    }

    // 수정 폼 페이지
    @GetMapping("/calendar/{date}/edit")
    public String editCalendar(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model,
                               RedirectAttributes rttr) {
        Optional<Calendar> optional = calendarRepository.findByDate(date);
        if (optional.isPresent()) {
            model.addAttribute("calendar", optional.get());
            return "calendars/edit";
        } else {
            rttr.addFlashAttribute("msg", "해당 날짜에 등록된 근무 정보가 없습니다.");
            return "redirect:/calendar";
        }
    }

    // 근무 정보 수정 처리
    @PostMapping("/calendar/{date}/update")
    public String updateCalendar(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 CalendarForm form,
                                 RedirectAttributes rttr) {
        Optional<Calendar> optional = calendarRepository.findByDate(date);
        if (optional.isPresent()) {
            Calendar target = optional.get();
            target.setMini(form.getMini());
            target.setStartTime(form.getStartTime());
            target.setEndTime(form.getEndTime());
            target.setBreakTime(form.getBreakTime());

            calendarRepository.save(target);
            rttr.addFlashAttribute("msg", "수정 완료되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "해당 날짜의 근무 데이터를 찾을 수 없습니다.");
        }

        return "redirect:/calendar/" + date;
    }

    // 근무 정보 삭제
    @PostMapping("/calendar/{date}/delete")
    public String deleteCalendar(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 RedirectAttributes rttr) {
        Optional<Calendar> optional = calendarRepository.findByDate(date);
        optional.ifPresent(calendarRepository::delete);
        return "redirect:/calendar";
    }

    // 한달마다 월급 계산
    @GetMapping("/calendar/wage")
    @ResponseBody
    public Map<String, Object> calculateMonthlyWage(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Calendar> calendars = calendarRepository.findByDateBetween(start, end.minusDays(1));
        long totalWage = 0;
        for (Calendar cal : calendars) {
            long workMinutes = Duration.between(cal.getStartTime(), cal.getEndTime()).toMinutes() - cal.getBreakTime();
            long mini = cal.getMini(); // 시급
            long hours = workMinutes / 60;
            long remainingMinutes = workMinutes % 60;
            double wagePerMinute = mini / 60.0;
            long wage = (hours * mini) + (long) Math.floor(remainingMinutes * wagePerMinute);
            totalWage += wage;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("totalWage", totalWage);
        return response;
    }

}
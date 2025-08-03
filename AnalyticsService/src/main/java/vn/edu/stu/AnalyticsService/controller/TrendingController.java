package vn.edu.stu.AnalyticsService.controller;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.stu.AnalyticsService.exception.ResourceNotFoundException;
import vn.edu.stu.AnalyticsService.response.WeeklyTrendingResponse;
import vn.edu.stu.AnalyticsService.service.WeeklyTrendingService;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class TrendingController {

    private final WeeklyTrendingService weeklyTrendingService;
    private static final Logger logger = LoggerFactory.getLogger(TrendingController.class);

    @GetMapping("/trending")
    public ResponseEntity<WeeklyTrendingResponse> getThisWeekTrending(
            @CookieValue(value = "accessToken") String accessToken) {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.ISO;
        int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
        int currentYear = today.get(weekFields.weekBasedYear());

        WeeklyTrendingResponse response = weeklyTrendingService.getWeeklyTrending(currentWeek, currentYear,
                accessToken);

        if (response == null) {
            logger.info("Không có dữ liệu trending");
            throw new ResourceNotFoundException("Không có dữ liệu trending nào!");
        }

        logger.info("ĐÃ LẤY ĐƯỢC DỮ LIỆU TRENDING: {}", response);
        return ResponseEntity.ok(response);
    }
}

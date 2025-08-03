package vn.edu.stu.WebBlogNauAn.utils;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Component;

@Component
public class TimeUtils {

    public Duration getDurationUntilThisSaturday2350() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        ZonedDateTime saturday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .withHour(23).withMinute(50).withSecond(0).withNano(0);

        return Duration.between(now, saturday);
    }
}

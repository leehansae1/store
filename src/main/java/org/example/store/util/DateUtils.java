package org.example.store.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static String getTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null || dateTime.getYear() == 1970) {
            return "";  // 메시지가 없거나 기본값일 때는 빈 문자열 반환
        }

        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(dateTime, now);
        long hoursBetween = ChronoUnit.HOURS.between(dateTime, now);
        long minutesBetween = ChronoUnit.MINUTES.between(dateTime, now);

        if (daysBetween == 0) {
            if (hoursBetween == 0) {
                if (minutesBetween < 1) {
                    return "방금 전";
                } else {
                    return minutesBetween + "분 전";
                }
            }
            return hoursBetween + "시간 전";
        } else {
            return daysBetween + "일 전";
        }
    }
}


package utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for date operations
 */
public class DateUtils {
    
    /**
     * Convert Date to LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                  .atZone(ZoneId.systemDefault())
                  .toLocalDateTime();
    }
    
    /**
     * Convert LocalDateTime to Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault())
                                .toInstant());
    }
    
    /**
     * Check if a date is within a given number of days from now
     */
    public static boolean isWithinDays(Date date, int days) {
        LocalDateTime dateTime = toLocalDateTime(date);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime daysAgo = now.minusDays(days);
        return !dateTime.isBefore(daysAgo) && !dateTime.isAfter(now);
    }
} 
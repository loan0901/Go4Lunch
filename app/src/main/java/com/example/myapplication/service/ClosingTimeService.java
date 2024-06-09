package com.example.myapplication.service;

import com.example.myapplication.Model.CustomOpeningHours;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;

public class ClosingTimeService {

    /**
     * This method displays the closing time of a restaurant based on its opening hours.
     *
     * @param openingHours The opening hours of the restaurant, encapsulated in a CustomOpeningHours object.
     * @return A string indicating the closing time if the restaurant is currently open, or the next opening time if it is closed.
     *         Returns "fermé aujourd'hui" if the restaurant is closed for the entire day.
     */
    public static String displayClosingTime(CustomOpeningHours openingHours) {
        // Check if opening hours or periods are null or empty
        if (openingHours == null || openingHours.periods == null || openingHours.periods.isEmpty()) {
            return "fermé aujourd'hui";
        }

        // Get the current date and time
        ZonedDateTime now = ZonedDateTime.now();
        DayOfWeek currentDayOfWeek = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();
        LocalTime nextOpeningTime = null;
        LocalTime nextClosingTime = null;

        // Iterate over each period in the opening hours
        for (CustomOpeningHours.Period period : openingHours.periods) {
            CustomOpeningHours.Point close = period.close;
            CustomOpeningHours.Point open = period.open;

            // Check if open and close points are not null and match the current day
            if (close != null && open != null && open.day == currentDayOfWeek.getValue()) {
                LocalTime closeTime = LocalTime.of(close.hour, close.minute);
                LocalTime openTime = LocalTime.of(open.hour, open.minute);

                // If the closing day is the same as the opening day
                if (close.day == open.day) {
                    // Closing on the same day
                    if (currentTime.isAfter(openTime) && closeTime.isAfter(currentTime)) {
                        // The restaurant is currently open
                        if (nextClosingTime == null || closeTime.isBefore(nextClosingTime)) {
                            nextClosingTime = closeTime;
                        }
                    } else if (currentTime.isBefore(openTime) && (nextOpeningTime == null || openTime.isBefore(nextOpeningTime))) {
                        // The restaurant is currently closed but will open later today
                        nextOpeningTime = openTime;
                    }
                } else {
                    // Closing after midnight
                    if (currentTime.isAfter(openTime) || currentTime.isBefore(closeTime)) {
                        // The restaurant is currently open
                        if (nextClosingTime == null || closeTime.isBefore(nextClosingTime)) {
                            nextClosingTime = closeTime;
                        }
                    } else if (currentTime.isBefore(openTime) && (nextOpeningTime == null || openTime.isBefore(nextOpeningTime))) {
                        // The restaurant is currently closed but will open later today
                        nextOpeningTime = openTime;
                    }
                }
            }
        }

        // Determine the closing or opening message
        if (nextClosingTime == null && nextOpeningTime == null) {
            return "fermé aujourd'hui";
        } else if (nextOpeningTime != null && (nextClosingTime == null || nextOpeningTime.isBefore(nextClosingTime))) {
            return "fermé, ouvre à " + nextOpeningTime.toString();
        } else {
            return "ferme à " + nextClosingTime.toString();
        }
    }
}
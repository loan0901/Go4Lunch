package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.myapplication.Model.CustomOpeningHours;
import com.example.myapplication.service.ClosingTimeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.ZonedDateTime;

import java.util.Arrays;
import java.util.Collections;

// Unit test class for ClosingTimeService, testing the displayClosingTime method.
// the goal is to retrieve the closest closing time if possible.
public class ClosingTimeServiceTest {

    // Mock context to simulate Android Context
    @Mock
    private Context mockContext;

    // Set up method to initialize mocks and define mock behavior
    @Before
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);
        // Define return values for mock context getString method
        when(mockContext.getString(R.string.close_today)).thenReturn("fermé aujourd'hui");
        when(mockContext.getString(R.string.close_at)).thenReturn("fermé à ");
        when(mockContext.getString(R.string.closed_opens_at)).thenReturn("fermé, ouvre à ");
    }

    // Test the displayClosingTime method when opening hours are null
    @Test
    public void testDisplayClosingTime_nullOpeningHours() {
        String result = ClosingTimeService.displayClosingTime(mockContext, null);
        assertEquals("fermé aujourd'hui", result);
    }

    // Test the displayClosingTime method when opening hours are empty
    @Test
    public void testDisplayClosingTime_emptyOpeningHours() {
        CustomOpeningHours openingHours = new CustomOpeningHours();
        openingHours.periods = Collections.emptyList();

        String result = ClosingTimeService.displayClosingTime(mockContext, openingHours);
        assertEquals("fermé aujourd'hui", result);
    }

    // Test the displayClosingTime method when the place is currently open
    @Test
    public void testDisplayClosingTime_currentlyOpen() {
        CustomOpeningHours.Period period = new CustomOpeningHours.Period();
        period.open = createPoint(DayOfWeek.MONDAY.getValue(), 9, 0);
        period.close = createPoint(DayOfWeek.MONDAY.getValue(), 17, 0);

        CustomOpeningHours openingHours = new CustomOpeningHours();
        openingHours.periods = Arrays.asList(period);

        ZonedDateTime mockNow = ZonedDateTime.now().with(DayOfWeek.MONDAY).withHour(10).withMinute(0);
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(ZonedDateTime::now).thenReturn(mockNow);

            String result = ClosingTimeService.displayClosingTime(mockContext, openingHours);
            assertEquals("fermé à 17:00", result);
        }
    }

    // Test the displayClosingTime method when the place is currently closed but opens later
    @Test
    public void testDisplayClosingTime_currentlyClosed_opensLater() {
        CustomOpeningHours.Period period = new CustomOpeningHours.Period();
        period.open = createPoint(DayOfWeek.MONDAY.getValue(), 17, 0);
        period.close = createPoint(DayOfWeek.MONDAY.getValue(), 23, 0);

        CustomOpeningHours openingHours = new CustomOpeningHours();
        openingHours.periods = Arrays.asList(period);

        ZonedDateTime mockNow = ZonedDateTime.now().with(DayOfWeek.MONDAY).withHour(10).withMinute(0);
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(ZonedDateTime::now).thenReturn(mockNow);

            String result = ClosingTimeService.displayClosingTime(mockContext, openingHours);
            assertEquals("fermé, ouvre à 17:00", result);
        }
    }

    // Test the displayClosingTime method when the place is closed all day
    @Test
    public void testDisplayClosingTime_closedAllDay() {
        CustomOpeningHours.Period period = new CustomOpeningHours.Period();
        period.open = createPoint(DayOfWeek.TUESDAY.getValue(), 9, 0);
        period.close = createPoint(DayOfWeek.TUESDAY.getValue(), 17, 0);

        CustomOpeningHours openingHours = new CustomOpeningHours();
        openingHours.periods = Arrays.asList(period);

        ZonedDateTime mockNow = ZonedDateTime.now().with(DayOfWeek.MONDAY).withHour(10).withMinute(0);
        try (MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {
            mockedZonedDateTime.when(ZonedDateTime::now).thenReturn(mockNow);

            String result = ClosingTimeService.displayClosingTime(mockContext, openingHours);
            assertEquals("fermé aujourd'hui", result);
        }
    }

    // Helper method to create a CustomOpeningHours.Point object
    private CustomOpeningHours.Point createPoint(int day, int hour, int minute) {
        CustomOpeningHours.Point point = new CustomOpeningHours.Point();
        point.day = day;
        point.hour = hour;
        point.minute = minute;
        return point;
    }
}

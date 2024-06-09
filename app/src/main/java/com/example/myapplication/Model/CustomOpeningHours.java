package com.example.myapplication.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomOpeningHours {
    @SerializedName("periods")
    public List<Period> periods;

    public static class Period {
        @SerializedName("open")
        public Point open;
        @SerializedName("close")
        public Point close;
    }

    public static class Point {
        @SerializedName("date")
        public Date date;
        @SerializedName("truncated")
        public boolean truncated;
        @SerializedName("day")
        public int day;
        @SerializedName("hour")
        public int hour;
        @SerializedName("minute")
        public int minute;
    }

    public static class Date {
        @SerializedName("year")
        public int year;
        @SerializedName("month")
        public int month;
        @SerializedName("day")
        public int day;
    }
}

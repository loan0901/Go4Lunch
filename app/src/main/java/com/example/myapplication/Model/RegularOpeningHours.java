package com.example.myapplication.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegularOpeningHours {

    @SerializedName("weekdayDescriptions")
    @Expose
    private List<String> weekdayDescriptions;

    public List<String> getWeekdayDescriptions() {
        return weekdayDescriptions;
    }

    public void setWeekdayDescriptions(List<String> weekdayDescriptions) {
        this.weekdayDescriptions = weekdayDescriptions;
    }

}
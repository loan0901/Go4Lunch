package com.example.myapplication.Model;

import java.util.List;

public class SearchRequestModel {
    public List<String> includedTypes;
    public int maxResultCount;
    public LocationRestriction locationRestriction;

    public static class LocationRestriction {
        public Circle circle;
    }

    public static class Circle {
        public Center center;
        public double radius;
    }

    public static class Center {
        public double latitude;
        public double longitude;
    }
}

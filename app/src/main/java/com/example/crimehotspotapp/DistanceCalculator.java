package com.example.crimehotspotapp;
import android.location.Location;

public class DistanceCalculator {
    public static float calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        Location startPoint = new Location("StartPoint");
        startPoint.setLatitude(startLatitude);
        startPoint.setLongitude(startLongitude);

        Location endPoint = new Location("EndPoint");
        endPoint.setLatitude(endLatitude);
        endPoint.setLongitude(endLongitude);
        return startPoint.distanceTo(endPoint);
    }
}

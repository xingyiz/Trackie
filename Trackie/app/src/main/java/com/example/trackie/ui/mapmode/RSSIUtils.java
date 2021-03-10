package com.example.trackie.ui.mapmode;

public class RSSIUtils {

   /* calculates line of sight distance using Propagation Path-Loss Model Estimate
    @param    A: Reference signal received at  distance d0 (measured power)
    @param    n: path attenuation factor
    @param    RSSI: rssi received from ScanResult

    */
    public static double calculateDistance(int RSSI, int A, int n) {
        double power = (A - RSSI)/(10*n);
        double distance = Math.pow(10, power);
        return distance;
    }

}

package com.example.trackie.ui.mapmode;

import static java.lang.Math.pow;

public class RSSIUtils {

   /* calculates line of sight distance using Propagation Path-Loss Model Estimate
    @param    A: Reference signal received at  distance d0 (measured power)
    @param    n: path attenuation factor
    @param    RSSI: rssi received from ScanResult

    */
    public static double calculateDistance(int RSSI, int A) {
        double n = 2.5;
        double power = (double) (A - RSSI)/(10*n);
        double distance = Math.pow(10, power);
        return distance;
    }

    /**
     * RSSI TO DISTANCE
     * @param RSSI in dBm
     * @param frequency
     * @return distance
     */
    public static double rssiToDistance(int RSSI, int frequency) {
        // https://electronics.stackexchange.com/questions/83354/calculate-distance-from-rssi

        // taking ref dist at 1m
        double d0 = 1.0;
        double d = 0.1;
        if ( RSSI > -50) {
            d = 0.5;
        } else if (RSSI > -60 && RSSI <= -50) {
            d = 1.0;
        } else if ( RSSI > -70 && RSSI <= -60) {
            d = 1.8;
        } else if (RSSI <= -70) {
            d = 2.5;
        }
        double FSPL = 20 * Math.log10(d) + 20 * Math.log10(frequency) - 27.55;
        double constant = 20 * Math.log10(4 * Math.PI * d0 / 0.125);
        double n = 2.5;
        return Math.pow(10, (FSPL - constant) * d0 / (10 * n));
    }

}

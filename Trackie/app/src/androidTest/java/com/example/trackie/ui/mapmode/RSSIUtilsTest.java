package com.example.trackie.ui.mapmode;

import org.junit.Test;

import static org.junit.Assert.*;

public class RSSIUtilsTest {

    @Test
    public void calculateDistance() {
        RSSIUtils r = new RSSIUtils();
        double distance = r.calculateDistance(-50, 3);
        assertTrue(distance == Math.pow(10, 2.12));
    }

    @Test
    public void rssiToDistance1() {
        RSSIUtils r = new RSSIUtils();
        double distance = r.rssiToDistance(-49, 84);
        double result = Math.pow(10, ((20 * Math.log10(0.5) + 20 * Math.log10(84) - 27.55) - (20 * Math.log10(4 * Math.PI * 1.0 / 0.125))) * 1.0 / (10 * 2.5));
        assertTrue(distance == result);
    }

    @Test
    public void rssiToDistance2() {
        RSSIUtils r = new RSSIUtils();
        double distance = r.rssiToDistance(-54, 109);
        double result = Math.pow(10, ((20 * Math.log10(1.0) + 20 * Math.log10(109) - 27.55) - (20 * Math.log10(4 * Math.PI * 1.0 / 0.125))) * 1.0 / (10 * 2.5));
        assertTrue(distance == result);
    }

    @Test
    public void rssiToDistance3() {
        RSSIUtils r = new RSSIUtils();
        double distance = r.rssiToDistance(-61, 92);
        double result = Math.pow(10, ((20 * Math.log10(1.8) + 20 * Math.log10(92) - 27.55) - (20 * Math.log10(4 * Math.PI * 1.0 / 0.125))) * 1.0 / (10 * 2.5));
        assertTrue(distance == result);
    }

    @Test
    public void rssiToDistance4() {
        RSSIUtils r = new RSSIUtils();
        double distance = r.rssiToDistance(-73, 87);
        double result = Math.pow(10, ((20 * Math.log10(2.5) + 20 * Math.log10(87) - 27.55) - (20 * Math.log10(4 * Math.PI * 1.0 / 0.125))) * 1.0 / (10 * 2.5));
        assertTrue(distance == result);
    }
}
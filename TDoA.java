package com.example.trackie.ui.mapmode;

 

public class TDoA {
    public LatLng getLocationByTrilateration(LatLng location1, double distance1, 
    LatLng location2, double distance2, LatLng location3, double distance3){

        double[] P1 = new double[2];
        double[] P2 = new double[2];
        double[] P3 = new double[2];
        double[] ex = new double[2];
        double[] ey = new double[2];
        double[] p3p1 = new double[2];
        double jval = 0;
        double temp = 0;
        double ival = 0;
        double p3p1i = 0;
        double triptx;
        double tripty;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        // translate each points to vectors
        P1[0] = location1.latitude;
        P1[1] = location1.longitude;

        P2[0] = location2.latitude;
        P2[1] = location2.longitude;

        P3[0] = location3.latitude;
        P3[1] = location3.longitude;

        // transform meters value for the map unit for distance between each point to location
        distance1 = (distance1 / 100000);
        distance2 = (distance2 / 100000);
        distance3 = (distance3 / 100000);

        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            t = t1 - t2;
            temp += (t*t);
        }
        
        d = Math.sqrt(temp);
        
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            exx = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t = t1 - t2 -t3;
            p3p1i += (t*t);
        }
        
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3)/Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1*t2);
        }

        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2))/(2*d);
        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2))/(2*jval)) - ((ival/jval)*xval);
        t1 = location1.latitude;
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;
        t1 = location1.longitude;
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        tripty = t1 + t2 + t3;
        return new LatLng(triptx,tripty);
    }

    // multi-network TDoA
    public static void calculateTDOA () {


    }

/*
    Calculates the CRLB bound
    :param x: Location
    :param anchor: Anchor locations
    :param sigma2: Variance
    :return: CRLB
*/

    public static void CRLB (float x,float anchor, float sigma2) {

    }



}

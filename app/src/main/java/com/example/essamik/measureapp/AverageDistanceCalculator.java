package com.example.essamik.measureapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageDistanceCalculator {

    private List<Double> mListRSSI;
    private double mCalibrationVal;

    private static Map<Double, Double> sCalibrationToCorrection;
    static {
        sCalibrationToCorrection = new HashMap();
        sCalibrationToCorrection.put(-115d, 0.6695652173913);
        sCalibrationToCorrection.put(-84d, 0.91666666666667);
        sCalibrationToCorrection.put(-81d, 0.95061728395062);
        sCalibrationToCorrection.put(-77d, 1d);
        sCalibrationToCorrection.put(-72d, 1.06944444444444);
        sCalibrationToCorrection.put(-69d, 1.11594202898551);
        sCalibrationToCorrection.put(-65d, 1.18461538461538);
        sCalibrationToCorrection.put(-59d, 1.30508474576271);
    }

    AverageDistanceCalculator(double calibrationVal) {
        mCalibrationVal = calibrationVal;
        mListRSSI = new ArrayList<>();

    }

    public void addDistance(double newMeasure) {
        if (mListRSSI.size() >= 10) {
            mListRSSI.remove(0);
        }
        mListRSSI.add(newMeasure);
    }

    public double getAveragedDistance() {
        double sum = 0;
        for (Double measure : mListRSSI) {
            sum += measure;
        }

        double avgRSSI = (sum/ mListRSSI.size());
        return computeMyDistance(avgRSSI, mCalibrationVal);
    }


    public static double computeMyDistance(double rssi, double calibrationValue) {
        double correctedRSSI = Math.abs(rssi) * (sCalibrationToCorrection.get(calibrationValue));
        double a = -1.809821e-01;
        double b = 5.462871e-02 * correctedRSSI;
        double c = -1.654253e-03 * Math.pow(correctedRSSI,2);
        double d = 1.817589e-06 * Math.pow(correctedRSSI,3);
        double e = 2.085460e-07 * Math.pow(correctedRSSI,4);
        double distance =  a+b+c+d+e;

        //Set minimum possible distance
        if (distance <= 0) distance = 0.01;
        return Math.round(distance * 100.0) / 100.0;
    }

}

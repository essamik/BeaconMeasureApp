package com.example.essamik.measureapp;

public class MeasurmentBeaconSignal {

    private String mMacAddress;
    private int milliSecElapsed;
    private double rssi;
    private double calibratedRSSI;
    private double distanceMeasured;
    private double realDistance;

    public MeasurmentBeaconSignal(String macAddress, int milliSec, double rssi, double calibratedRSSI, double distanceMeasured, double realDistance) {
        this.mMacAddress = macAddress;
        this.milliSecElapsed = milliSec;
        this.rssi = rssi;
        this.calibratedRSSI = calibratedRSSI;
        this.distanceMeasured = distanceMeasured;
        this.realDistance = realDistance;
    }

    public String toCSV() {
        return mMacAddress + "," + milliSecElapsed + "," + rssi + ","+ (rssi/calibratedRSSI) + "," + distanceMeasured + "," + realDistance + "\n";
    }

    public static String getCSVHeader() {
        return "macAddress,timeEslapsed,RSSI, ratioRSSI, calculatedDistance,realDistance\n";
    }
}
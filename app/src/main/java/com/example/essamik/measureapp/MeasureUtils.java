package com.example.essamik.measureapp;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.style.TtsSpan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class MeasureUtils {

    public static int averageIntegerValues(List<Integer> listValues) {
        int n = listValues.size();
        int sum = 0;
        for (Integer value : listValues) {
            sum += value;
        }
        return sum / n;
    }

    public static double averageDoubleValues(List<Double> listValues) {
        double n = listValues.size();
        double sum = 0;
        for (Double value : listValues) {
            sum += value;
        }
        return sum / n;
    }

    public static void saveMeasure(String content, double distance, MyBeacon beacon, String libraryName) {
        String filename = libraryName + " " + beacon.getMajor() + "-" + beacon.getMinor() + " (" + distance + "m).csv";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_measures");
        myDir.mkdirs();
        File file = new File (myDir, filename);
        if (file.exists ()) file.delete ();

        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));
            out.write(content);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String extractCSVData(List<MeasurmentBeaconSignal> listMeasure) {
        StringBuilder builder = new StringBuilder(MeasurmentBeaconSignal.getCSVHeader());

        for (MeasurmentBeaconSignal measure : listMeasure) {
            builder.append(measure.toCSV());
        }

        return builder.toString();
    }

    public static double computeMyDistance(double rssi) {
        double a = 28.233069063;
        double b = -(0.954435316 * Math.abs(rssi));
        double c = 0.008031245 * Math.pow(Math.abs(rssi),2);
        double distance =  a+b+c;
        return Math.round(distance * 100.0) / 100.0;
    }
}

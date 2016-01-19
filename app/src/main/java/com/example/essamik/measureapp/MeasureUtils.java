package com.example.essamik.measureapp;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class MeasureUtils {

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
}

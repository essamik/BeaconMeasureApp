package com.example.essamik.measureapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.swisscom.beacondistanceestimator.AverageDistanceEstimator;

public class MeasureFragment extends Fragment {

    public static final int TIME_SCANNING_IN_MS = 60000;

    private MyBeacon mBeaconToMeasure;
    private Toolbar mToolBar;
    private String mLibraryName;
    private List<MeasurmentBeaconSignal> mListMeasurment;
    boolean mIsMeasuring = false;
    private int mMilliSecondsStart;
    private double mRealDistance;
    private AverageDistanceEstimator mDistanceCalculator;
    private boolean mIsComparing;

    private TextView mAddressLabel;
    private TextView mSDKDistanceLabel;
    private TextView mMyDistanceLabel;
    private TextView mMajminLabel;
    private TextView mRSSILabel;
    private EditText mRealDistanceInput;
    private Button mStartMeasuringButton;
    private Button mCompareButton;


    public static MeasureFragment newInstance(Context context, MyBeacon beacon, String libraryName) {
        MeasureFragment instance = new MeasureFragment();
        instance.mBeaconToMeasure = beacon;
        instance.mLibraryName = libraryName;
        instance.mListMeasurment = new ArrayList<>();
        instance.mDistanceCalculator = new AverageDistanceEstimator(context, beacon.getCalibrationVal());

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.beacon_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolBar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        mAddressLabel = (TextView) getView().findViewById(R.id.detail_address);
        mSDKDistanceLabel = (TextView) getView().findViewById(R.id.detail_distance);
        mMyDistanceLabel = (TextView) getView().findViewById(R.id.detail_mydistance);
        mMajminLabel = (TextView) getView().findViewById(R.id.detail_majmin);
        mRSSILabel = (TextView) getView().findViewById(R.id.detail_rssi);
        mRealDistanceInput = (EditText) getView().findViewById(R.id.detail_realdistance_input);
        mStartMeasuringButton = (Button) getView().findViewById(R.id.detail_startmeasuring_button);
        mCompareButton = (Button) getView().findViewById(R.id.detail_compare_button);
        mStartMeasuringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsMeasuring = true;
                mMilliSecondsStart = (int) (System.currentTimeMillis());
                mRealDistance = Double.parseDouble(mRealDistanceInput.getText().toString());

                mStartMeasuringButton.setEnabled(false);

                new CountDownTimer(TIME_SCANNING_IN_MS, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mToolBar.setSubtitle(millisUntilFinished / 1000 + "s to finish...");
                    }

                    @Override
                    public void onFinish() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stopMeasuring();
                                mToolBar.setSubtitle("");

                            }
                        });
                    }
                }.start();

            }
        });

        mCompareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRealDistance = Double.parseDouble(mRealDistanceInput.getText().toString());
                mIsComparing = true;
            }
        });

        fillVew();
    }

    private void stopMeasuring() {
        Toast.makeText(getActivity(), "Finished Measuring", Toast.LENGTH_SHORT).show();
        mIsMeasuring = false;
        mStartMeasuringButton.setEnabled(true);

        MeasureUtils.saveMeasure(MeasureUtils.extractCSVData(mListMeasurment), mRealDistance, mBeaconToMeasure, mLibraryName);
    }

    private void fillVew() {
        double sdkDistance = mBeaconToMeasure.getDistance();
        double myDistance = mDistanceCalculator.getAveragedDistance();
        boolean myDistanceIsBetter = Math.abs(myDistance - mRealDistance) <= Math.abs(sdkDistance - mRealDistance);
        double myDistanceRaw = AverageDistanceEstimator.calculateDistance(mBeaconToMeasure.getRssi(), mBeaconToMeasure.getCalibrationVal());

        if (mIsComparing) {
            mSDKDistanceLabel.setTextColor(myDistanceIsBetter ? Color.RED : Color.GREEN);
            mMyDistanceLabel.setTextColor(myDistanceIsBetter ? Color.GREEN : Color.RED);
        }

        mAddressLabel.setText("Address : " + mBeaconToMeasure.getAddress());
        mMajminLabel.setText("Major : " + mBeaconToMeasure.getMajor() + " Minor : " + mBeaconToMeasure.getMinor());
        mRSSILabel.setText("RSSI : " + mBeaconToMeasure.getRssi() + "dBm");
        mSDKDistanceLabel.setText("SDK Distance : " + mBeaconToMeasure.getDistance() + "m");
        mMyDistanceLabel.setText("My Distance : " + myDistance + "m ("+ myDistanceRaw +"m) / " + mDistanceCalculator.getSampleSize());
    }

    public void onSignalReceived(MyBeacon beacon) {
        mBeaconToMeasure = beacon;
        mDistanceCalculator.addRSSI(beacon.getRssi());

        if (getView() != null) fillVew();

        if (mIsMeasuring) {
            int millisecElapsed = (int) (System.currentTimeMillis()) - mMilliSecondsStart;
            mListMeasurment.add(new MeasurmentBeaconSignal(mBeaconToMeasure.getAddress(), millisecElapsed, beacon.getRssi(), beacon.getCalibrationVal(), beacon.getDistance(), mDistanceCalculator.getAveragedDistance(),  mRealDistance));
        }
    }
}

package com.example.essamik.measureapp;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BeaconAdapter extends BaseAdapter {

    private ArrayList<MyBeacon> mBeaconList;
    private LayoutInflater mInflater;
    private Toolbar mToolbar;

    public BeaconAdapter(Activity activity, Toolbar toolbar) {
        this.mInflater = LayoutInflater.from(activity);
        this.mToolbar = toolbar;
        this.mBeaconList = new ArrayList<>();
    }

    public void replaceWith(MyBeacon newBeacon) {
        boolean found = false;

        for (int i = 0; i < mBeaconList.size(); i++) {
            if (mBeaconList.get(i).getAddress().equals(newBeacon.getAddress())) {
                mBeaconList.set(i, newBeacon);
                found = true;
                break;
            }
        }

        if (!found) {
            mBeaconList.add(newBeacon);
        }

        Collections.sort(mBeaconList, new Comparator<MyBeacon>() {
            @Override
            public int compare(MyBeacon beacon1, MyBeacon beacon2) {
                return Double.compare(beacon1.getDistance(), beacon2.getDistance());
            }
        });

        mToolbar.setSubtitle("Found Beacons: " + mBeaconList.size());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBeaconList.size();
    }

    @Override
    public MyBeacon getItem(int position) {
        return mBeaconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(MyBeacon beacon, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.macTextView.setText(String.format(beacon.getAddress() + " (" + beacon.getDistance() + "m)"));
        holder.majorTextView.setText("Major: " + beacon.getMajor());
        holder.minorTextView.setText("Minor: " + beacon.getMinor());
        holder.measuredPowerTextView.setText("Calibrated : " + beacon.getCalibrationVal());
        holder.rssiTextView.setText("RSSI: " + beacon.getRssi());
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.item_beacon, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final TextView macTextView;
        final TextView majorTextView;
        final TextView minorTextView;
        final TextView measuredPowerTextView;
        final TextView rssiTextView;

        ViewHolder(View view) {
            macTextView = (TextView) view.findViewWithTag("mac");
            majorTextView = (TextView) view.findViewWithTag("major");
            minorTextView = (TextView) view.findViewWithTag("minor");
            measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
            rssiTextView = (TextView) view.findViewWithTag("rssi");
        }
    }
}

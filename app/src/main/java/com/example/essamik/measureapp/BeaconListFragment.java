package com.example.essamik.measureapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class BeaconListFragment extends Fragment {

    private BeaconAdapter mBeaconAdapter;
    private OnBeaconSelectedListener mBeaconListener;

    public interface OnBeaconSelectedListener {
        void onBeaconSelected(MyBeacon beacon);
    }

    public static BeaconListFragment newInstance(BeaconAdapter adapter, OnBeaconSelectedListener listener) {
        BeaconListFragment instance = new BeaconListFragment();
        instance.mBeaconAdapter = adapter;
        instance.mBeaconListener = listener;

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_beacons, container, false);

        ListView listView = (ListView) view.findViewById(R.id.main_listview);
        listView.setAdapter(mBeaconAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyBeacon beaconSelected = (MyBeacon) parent.getItemAtPosition(position);
                mBeaconListener.onBeaconSelected(beaconSelected);
            }
        });

        return view;
    }
}

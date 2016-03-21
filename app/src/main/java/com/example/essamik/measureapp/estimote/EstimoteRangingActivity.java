package com.example.essamik.measureapp.estimote;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.example.essamik.measureapp.BaseActivity;
import com.example.essamik.measureapp.MyBeacon;
import com.example.essamik.measureapp.R;

import java.util.List;
import java.util.UUID;

public class EstimoteRangingActivity extends BaseActivity {

    protected static final Region ALL_BEACONS_REGION = new Region("myregion", null, null, null);
    public final String LIBRARY_NAME = "Estimote";

    private static final int REQUEST_PERMISSION_BT = 42;
    private static final int REQUEST_ENABLE_BT = 1234;

    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        if (mMeasureFragment != null && beacons.size() > 0) {
                            mMeasureFragment.onSignalReceived(MyBeacon.fromEstimote(beacons.get(0)));
                        } else {
                            for (Beacon beacon : beacons) {
                                mBeaconAdapter.replaceWith(MyBeacon.fromEstimote(beacon));
                            }
                        }
                    }
                });
            }
        });

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, R.string.error_no_ble, Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            checkCoarseLocationPermission();
        }
    }

    private void checkCoarseLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            connectToService();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_BT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_BT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectToService();
                }
                break;
        }
    }

    private void connectToService() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                mToolBar.setSubtitle(R.string.scanning);
                beaconManager.startRanging(ALL_BEACONS_REGION);
            }
        });
    }


    @Override
    protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolBar.setTitle(getString(R.string.app_name) + " - " + LIBRARY_NAME);
    }

    @Override
    protected void onStop() {
        beaconManager.stopRanging(ALL_BEACONS_REGION);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                checkCoarseLocationPermission();
            } else {
                Toast.makeText(this, R.string.error_ble_off, Toast.LENGTH_LONG).show();
                mToolBar.setSubtitle(R.string.error_ble_off);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public String getLibraryName() {
        return LIBRARY_NAME;
    }

    @Override
    protected void startRangingAll() {
        beaconManager.startRanging(ALL_BEACONS_REGION);
    }

    @Override
    protected void onRegionChange(MyBeacon beacon) {
        beaconManager.stopRanging(ALL_BEACONS_REGION);
        beaconManager.startRanging(new Region("myregion", UUID.fromString(beacon.getUuid()), beacon.getMajor(), beacon.getMinor()));
    }

}

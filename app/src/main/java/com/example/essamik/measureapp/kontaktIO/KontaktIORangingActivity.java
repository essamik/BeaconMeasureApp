package com.example.essamik.measureapp.kontaktIO;

import android.os.Bundle;
import android.widget.Toast;

import com.example.essamik.measureapp.BaseActivity;
import com.example.essamik.measureapp.MyBeacon;
import com.example.essamik.measureapp.R;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.DistanceSort;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconAdvertisingPacket;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilter;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.common.BuildConfig;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.log.LogLevel;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KontaktIORangingActivity extends BaseActivity implements ProximityManager.ProximityListener {

    public static final String LIBRARY_NAME = "Kontakt.io";

    private ProximityManager mProximityManager;
    private OnServiceReadyListener mServiceListener = new OnServiceReadyListener() {
        @Override
        public void onServiceReady() {
            mProximityManager.attachListener(KontaktIORangingActivity.this);
        }

        @Override
        public void onConnectionFailure() {
            Toast.makeText(getApplicationContext(), "Connection Failure", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KontaktSDK.initialize(this)
                .setDebugLoggingEnabled(BuildConfig.DEBUG)
                .setLogLevelEnabled(LogLevel.DEBUG, true)
                .setCrashlyticsLoggingEnabled(true);
        KontaktSDK.initialize("ZdOFMRNcHXAScddULTrYnfOWXjvBOJvI");

        startRangingAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolBar.setTitle(getString(R.string.app_name) + " - "  + LIBRARY_NAME);
    }

    @Override
    public void onScanStart() {

    }

    @Override
    public void onScanStop() {

    }

    @Override
    public void onEvent(BluetoothDeviceEvent event) {
        final IBeaconDeviceEvent iBeaconDeviceEvent = (IBeaconDeviceEvent) event;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IBeaconRegion region = iBeaconDeviceEvent.getRegion();
                List<IBeaconDevice> deviceList = iBeaconDeviceEvent.getDeviceList();
                // Note that beacons reported here are already sorted by estimated
                // distance between device and beacon.
                if (mMeasureFragment != null && deviceList.size() > 0) {
                    mMeasureFragment.onSignalReceived(MyBeacon.fromKontaktIO(deviceList.get(0)));
                } else {
                    for (IBeaconDevice beaconDevice : deviceList) {
                        mBeaconAdapter.replaceWith(MyBeacon.fromKontaktIO(beaconDevice));
                    }
                }
            }
        });
    }

    @Override
    protected void onRegionChange(final MyBeacon beacon) {
        List<IBeaconFilter> filterList = Arrays.asList(
                IBeaconFilters.newProximityUUIDFilter(UUID.fromString(beacon.getUuid())),
                IBeaconFilters.newMajorFilter(beacon.getMajor()),
                IBeaconFilters.newMinorFilter(beacon.getMinor())

        );

        IBeaconScanContext ibeaconScanContext = new IBeaconScanContext.Builder()
                .setDevicesUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(2))
                .setDistanceSort(DistanceSort.DESC)
                .setIBeaconFilters(Collections.singleton(new IBeaconFilter() {
                    @Override
                    public boolean apply(IBeaconAdvertisingPacket iBeaconAdvertisingPacket) {
                        final UUID proximityUUID = iBeaconAdvertisingPacket.getProximityUUID();
                        final int major = iBeaconAdvertisingPacket.getMajor();
                        final int minor = iBeaconAdvertisingPacket.getMinor();

                        return proximityUUID.equals(UUID.fromString(beacon.getUuid())) &&  major == beacon.getMajor() && minor == beacon.getMinor();
                    }
                }))

                .build();

        ScanContext scanContext = new ScanContext.Builder()
                .setIBeaconScanContext(ibeaconScanContext)
                .setForceScanConfiguration(ForceScanConfiguration.DEFAULT)
                .setScanPeriod(new ScanPeriod(3000, 0))
                .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                .build();

        mProximityManager.finishScan();
        mProximityManager.detachListener(this);
        mProximityManager.disconnect();

        mProximityManager = new ProximityManager(this);
        mProximityManager.initializeScan(scanContext, mServiceListener);
    }

    @Override
    protected void startRangingAll() {
        if (mProximityManager != null) {
            mProximityManager.detachListener(this);
            mProximityManager.disconnect();
        }

        mProximityManager = new ProximityManager(this);

        IBeaconScanContext iBeaconScanContext = new IBeaconScanContext.Builder()
                .setRssiCalculator(RssiCalculators.newLimitedMeanRssiCalculator(5))
                .setDevicesUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(2))
                .setDistanceSort(DistanceSort.DESC).build();

        ScanContext scanContext = new ScanContext.Builder()
                .setIBeaconScanContext(iBeaconScanContext)
                .setForceScanConfiguration(ForceScanConfiguration.DEFAULT)
                .setScanPeriod(new ScanPeriod(3000, 0))
                .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                .build();

        mProximityManager.initializeScan(scanContext, mServiceListener);

        mToolBar.setSubtitle("Scanning...");
    }

    @Override
    protected String getLibraryName() {
        return LIBRARY_NAME;
    }

}

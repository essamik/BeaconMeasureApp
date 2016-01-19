package com.example.essamik.measureapp.altbeacon;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.example.essamik.measureapp.BaseActivity;
import com.example.essamik.measureapp.MyBeacon;
import com.example.essamik.measureapp.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class AltBeaconRangingActivity extends BaseActivity implements BeaconConsumer {

    protected static final Region ALL_BEACONS_REGION = new Region("myregion", null, null, null);
    public final String LIBRARY_NAME = getString(R.string.altbeacon);

    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure BeaconManager.
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon type.
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        mBeaconManager.bind(this);

    }

    @Override
    protected void onRegionChange(MyBeacon beacon) {
        try {
            mBeaconManager.stopRangingBeaconsInRegion(ALL_BEACONS_REGION);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mBeaconManager.startRangingBeaconsInRegion(new Region("myregion",
                    Identifier.parse(beacon.getUuid()),
                    Identifier.fromInt(beacon.getMajor()),
                    Identifier.fromInt(beacon.getMinor())));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLibraryName() {
        return LIBRARY_NAME;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolBar.setTitle(getString(R.string.app_name) + " - "  + LIBRARY_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (Beacon beaconDevice : beacons) {
                            if (mMeasureFragment != null) {
                                mMeasureFragment.onSignalReceived(MyBeacon.fromAltBeacon(beaconDevice));
                            } else {
                                mBeaconAdapter.replaceWith(MyBeacon.fromAltBeacon(beaconDevice));
                            }
                        }
                    }
                });
            }
        });

        startRangingAll();
    }

    protected void startRangingAll() {
        try {
            mBeaconManager.startRangingBeaconsInRegion(ALL_BEACONS_REGION);
            mToolBar.setSubtitle(R.string.scanning);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

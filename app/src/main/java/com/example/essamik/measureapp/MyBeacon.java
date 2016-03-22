package com.example.essamik.measureapp;

import com.estimote.sdk.Utils;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import org.altbeacon.beacon.Beacon;

import java.io.Serializable;

public class MyBeacon implements Serializable {

    private String mUuid;
    private int mMajor;
    private int mMinor;
    private String mAdress;
    private double mRssi;
    private int mBatteryPower;
    private double mDistance;
    private double mCalibrationVal;
    private String mName;

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }

    public int getMajor() {
        return mMajor;
    }

    public void setMajor(int major) {
        this.mMajor = major;
    }

    public int getMinor() {
        return mMinor;
    }

    public void setMinor(int minor) {
        this.mMinor = minor;
    }

    public String getAddress() {
        return mAdress;
    }

    public void setAddress(String address) {
        this.mAdress = address;
    }

    public double getRssi() {
        return mRssi;
    }

    public void setRssi(double rssi) {
        this.mRssi = rssi;
    }

    public double getBatteryPower() {
        return mBatteryPower;
    }

    public void setBatteryPower(int power) {
        mBatteryPower = power;
    }

    public double getDistance() {
        return Math.round(mDistance * 100.0) / 100.0;
    }

    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    public double getCalibrationVal() {
        return mCalibrationVal;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }


    public static MyBeacon fromAltBeacon(Beacon baseBeacon) {
        MyBeacon myBeacon = new MyBeacon();
        myBeacon.mUuid = baseBeacon.getId1().toString();
        myBeacon.mMajor = baseBeacon.getId2().toInt();
        myBeacon.mMinor = baseBeacon.getId3().toInt();
        myBeacon.mAdress = baseBeacon.getBluetoothAddress();
        myBeacon.mDistance =  baseBeacon.getDistance();
        myBeacon.mRssi = baseBeacon.getRssi();
        myBeacon.mCalibrationVal = baseBeacon.getTxPower();

        return myBeacon;
    }


    public static MyBeacon fromEstimote(com.estimote.sdk.Beacon baseBeacon) {
        MyBeacon myBeacon = new MyBeacon();
        myBeacon.mUuid = baseBeacon.getProximityUUID().toString();
        myBeacon.mMajor = baseBeacon.getMajor();
        myBeacon.mMinor = baseBeacon.getMinor();
        myBeacon.mAdress = baseBeacon.getMacAddress().toStandardString();
        myBeacon.mDistance =  Utils.computeAccuracy(baseBeacon);
        myBeacon.mRssi = baseBeacon.getRssi();
        myBeacon.mCalibrationVal = baseBeacon.getMeasuredPower();

        return myBeacon;
    }


    public static MyBeacon fromKontaktIO(IBeaconDevice baseBeacon) {
        MyBeacon myBeacon = new MyBeacon();
        myBeacon.mUuid = baseBeacon.getProximityUUID().toString();
        myBeacon.mName = baseBeacon.getUniqueId();
        myBeacon.mMajor = baseBeacon.getMajor();
        myBeacon.mMinor = baseBeacon.getMinor();
        myBeacon.mAdress = baseBeacon.getAddress();
        myBeacon.mDistance =  baseBeacon.getDistance();
        myBeacon.mRssi = baseBeacon.getRssi();
        myBeacon.mBatteryPower = baseBeacon.getBatteryPower();
        myBeacon.mCalibrationVal = baseBeacon.getTxPower();

        return myBeacon;
    }
}

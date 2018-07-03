package com.wedevgroup.weflyhelper.service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.wedevgroup.weflyhelper.presenter.BaseActivity;
import com.wedevgroup.weflyhelper.presenter.BaseService;
import com.wedevgroup.weflyhelper.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by Obrina.KIMI on 1/10/2018.
 */

public class LocationProviderService extends BaseService implements LocationListener {
    private final String TAG = getClass().getSimpleName();
    ScheduledExecutorService scheduler;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;

    Intent intent;
    public static String str_receiver = "gps.service.receiver";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(str_receiver);

        scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        // send time change Request
                        // next time
                        refreshPosition();
                    }
                }, 0, 1, TimeUnit.SECONDS);
        //first Time
        refreshPosition();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    private void notifyUpdate(Location location){
        intent.putExtra("latitude",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        // update All activity
        BaseActivity.setuLatitude(location.getLatitude());
        BaseActivity.setuLongitude(location.getLongitude());
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        turnOff();
        super.onDestroy();
    }

    private void turnOff() {
        if(scheduler != null){
            scheduler.shutdown();
            scheduler = null;
        }
    }

    private void refreshPosition() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnable && !isNetworkEnable) {

            } else {

                if (isNetworkEnable) {
                    location = null;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                        if (locationManager!=null){
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location!=null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                notifyUpdate(location);
                            }
                        }
                        return;
                    }
                }

                if (isGPSEnable){
                    location = null;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                    if (locationManager!=null){
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location!=null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            notifyUpdate(location);
                        }
                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}

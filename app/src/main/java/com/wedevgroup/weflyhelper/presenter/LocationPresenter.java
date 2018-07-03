package com.wedevgroup.weflyhelper.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.utils.Constants;

/**
 * Created by admin on 19/02/2018.
 */

public class LocationPresenter {

    private GoogleApiAvailability googleApiAvailability;
    private LocationManager mLocationManager;
    private Activity activity;


    public LocationPresenter(final @NonNull Activity activity){
        this.activity = activity;
        googleApiAvailability = GoogleApiAvailability.getInstance();
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }
    public void showLocationServicesRequireDialog() {
        String title = activity.getString(R.string.mlocation_permission_dialog_title);
        String message = activity.getString(R.string.location_service_disable);
        String negativeButtonText = activity.getString(R.string.not_now);
        String positiveButtonText = activity.getString(R.string.yes);
        new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                })
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openLocationSettings();
                    }
                })
                .create().show();
    }

    public void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, Constants.ENABLE_LOCATION_SERVICES_REQUEST);
    }

    public boolean isPermissionGranted() {
        if (!hasLocationPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
                showPermissionRequireDialog();
            else
                requestPermission();
            return false;
        } else{
            //onPermissionGranted();
            return  true;
        }

    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void showPermissionRequireDialog() {
        String title = activity.getString(R.string.mlocation_permission_dialog_title);
        String message = activity.getString(R.string.location_service_disable);
        String negativeButtonTitle = activity.getString(R.string.not_now);
        String positiveButtonTitle = activity.getString(R.string.yes);
        new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                })
                .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission();
                    }
                }).create().show();
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSIONS_REQUEST);
    }

    public boolean canRequestLocation() {
        if (isGoogleServiceAvailable()) {
            return isPermissionGranted();
        } else{
            showGooglePlayServicesErrorDialog();
            return false;
        }
    }

    public boolean isGoogleServiceAvailable() {
        return googleApiAvailability.isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS;
    }

    private void showGooglePlayServicesErrorDialog() {
        int errorCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (googleApiAvailability.isUserResolvableError(errorCode))
            googleApiAvailability.getErrorDialog(activity, errorCode, Constants.GOOGLE_PLAY_SERVICES_ERROR_DIALOG).show();
    }

    public boolean isLocationEnabled() {
        return isGPSLocationEnabled()
                || isNetworkLocationEnabled();
    }

    public boolean isGPSLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }



}

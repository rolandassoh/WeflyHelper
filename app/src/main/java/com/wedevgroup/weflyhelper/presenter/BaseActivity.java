package com.wedevgroup.weflyhelper.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.wedevgroup.weflyhelper.activity.LoginActivity;
import com.wedevgroup.weflyhelper.interfaces.MainAction;
import com.wedevgroup.weflyhelper.service.LocationProviderService;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.NetworkWatcher;
import com.wedevgroup.weflyhelper.utils.PermissionUtil;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 21/03/2018.
 */

public class BaseActivity extends LanguagePresenter implements NetworkWatcher.OnInternetListener , MainAction{
    public static Double uLatitude = Constants.DOUBLE_NULL;
    public static Double uLongitude = Constants.DOUBLE_NULL;
    protected PermissionUtil pUtil;
    protected static boolean isAllPermissionGranted = false;
    private static boolean showPermissionDialog;
    private static boolean isFirstTime = true;
    protected AppController appController;
    protected NetworkWatcher watcher;
    protected boolean isReqDone;
    private String TAG = getClass().getSimpleName();
    // Support vector
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        setDefaultLanguage(Locale.FRENCH);
        super.onCreate(savedInstanceState);

        pUtil = new PermissionUtil(this);
        // Access
        appController = AppController.getInstance();
        //check permission
        isAllPermissionGranted = pUtil.isAllPermissionsGranded();
        if(!isAllPermissionGranted){
            if(isFirstTime){
                showPermissionDialog = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onRequestAllPermissions();
                            isReqDone = true;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(1));
                isFirstTime = false;
            }
        }else
            isReqDone = true;

    }

    protected void onMoveCameraToPosition(@NonNull GoogleMap map, @NonNull LatLng latLng){
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public static boolean isAllPermissionGranted() {
        return isAllPermissionGranted;
    }

    public static void setIsAllPermissionGranted(boolean isAllPermissionGranted) {
        BaseActivity.isAllPermissionGranted = isAllPermissionGranted;
    }


    public void onRequestAllPermissions(){
        //request Permission
        pUtil.requestAllPermissions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Listen time
        startLocationService();
        if(showPermissionDialog)
            pUtil.requestAllPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, LocationProviderService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_APP_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    showPermissionDialog = false;
                    isAllPermissionGranted = true;
                } else {
                    showPermissionDialog = true;
                    // Display on Next launch
                    isFirstTime = true;
                    try{
                        pUtil.onAllPermissionError(pUtil.getPermissionsDenied());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void startLocationService(){
        Intent intent = new Intent(this, LocationProviderService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startService(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Double getCurrentLatitude(){
        return uLatitude;
    }

    public static Double getCurrentLongitude(){
        return uLongitude;
    }

    public static void setuLatitude(@NonNull Double uLatitude) {
        BaseActivity.uLatitude = uLatitude;
    }

    public static void setuLongitude(@NonNull Double uLongitude) {
        BaseActivity.uLongitude = uLongitude;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onNotConnected() {

    }

    @Override
    public void onRetry() {

    }

    @Override
    public void onDisplayMainActivity(Activity activity) {
        new MainActivityClass().onDisplayMainActivity(activity);
    }

    protected void removeAsynTasks(){
        AppController.clearAsynTask();
    }


    protected void checkTokenAndNetwork(@NonNull final BaseActivity activity, @NonNull final NetworkWatcher w){
        if (appController != null){
            if (appController.isTokenValide()){
                if (w != null)
                    w.isNetworkAvailable();
            }else {
                startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            }
        }

    }

    public void launchActivity(@NonNull final  Intent intent, @NonNull final  Activity act, boolean withAnimation){
        if (appController != null){
            if (appController.isTokenAndUserOk(act)){
                Utils utils = new Utils();
                startActivity(intent);
                if (withAnimation){
                    utils.animActivityOpen(act);
                }
            }
        }
    }
}

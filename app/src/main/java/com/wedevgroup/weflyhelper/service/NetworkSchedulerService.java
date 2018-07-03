package com.wedevgroup.weflyhelper.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.wedevgroup.weflyhelper.receiver.ConnectivityReceiver24Up;
import com.wedevgroup.weflyhelper.utils.Constants;

/**
 * Created by admin on 07/05/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver24Up.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();

    private ConnectivityReceiver24Up mConnectivityReceiver24Up;

    @Override
    public void onCreate() {
        super.onCreate();

        mConnectivityReceiver24Up = new ConnectivityReceiver24Up(this);
    }



    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        registerReceiver(mConnectivityReceiver24Up, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unregisterReceiver(mConnectivityReceiver24Up);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected){
            startService(new Intent(this, PostParcelleService.class));
        }
    }
}
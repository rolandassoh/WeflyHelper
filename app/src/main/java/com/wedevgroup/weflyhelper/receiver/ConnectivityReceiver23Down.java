package com.wedevgroup.weflyhelper.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.wedevgroup.weflyhelper.service.PostParcelleService;
import com.wedevgroup.weflyhelper.utils.Constants;

/**
 * Created by admin on 23/03/2018.
 */

public class ConnectivityReceiver23Down extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();
    boolean isServiceRunning;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            try {
                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (PostParcelleService.class.getName().equals(service.service.getClassName())) {
                        isServiceRunning = true;
                    }
                }

                if (!isServiceRunning)
                    context.startService(new Intent(context, PostParcelleService.class));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

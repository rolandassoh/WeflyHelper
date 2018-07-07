package com.wedevgroup.weflyhelper.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wedevgroup.weflyhelper.R;

/**
 * Created by admin on 26/04/2018.
 */

public class NetworkWatcher {
    private Context ctx;
    private View v;
    private final AppController appController;
    private String TAG = getClass().getSimpleName();
    private OnInternetListener listener;
    public NetworkWatcher(final Context ctx, @NonNull View view){
        this.ctx = ctx;
        appController = AppController.getInstance();
        this.v = view;
    }


    public  void isNetworkAvailable() {
        if (appController.isNetworkAvailable()){
            notifyOnInternetListener(true, false);
        }else {
            try {
                notifyOnInternetListener(false, false);
                Snackbar snackbar = Snackbar
                        .make(v, ctx.getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(ctx.getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                notifyOnInternetListener(false, true);
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(ctx, R.color.white));
                snackbar.show();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public  void isNetworkAvailableWithSilent() {
        if (appController.isNetworkAvailable()){
            notifyOnInternetListener(true, false);
        }
    }


    public void setOnInternetListener(@NonNull OnInternetListener listener) {
        this.listener = listener;

    }

    public static interface OnInternetListener {
        void onConnected();
        void onNotConnected();
        void onRetry();
    }

    private void notifyOnInternetListener(boolean isOK, boolean shouldRetry) {
        if (listener != null){
            try {
                if (shouldRetry)
                    listener.onRetry();
                else {
                    if (isOK){
                        listener.onConnected();
                    }else{
                        listener.onNotConnected();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

package com.wedevgroup.weflyhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.presenter.LoadingPresenter;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.NetworkWatcher;
import com.wedevgroup.weflyhelper.utils.Utils;

public class LoadingActivity extends DBActivity {
    private CircleProgress progressC;
    private LoadingPresenter presenter;
    private final String TAG = getClass().getSimpleName();
    private RelativeLayout layout;
    private TextView percent;
    private Utils util;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressC       = (CircleProgress) findViewById(R.id.circle_progress);
        layout          = (RelativeLayout) findViewById(R.id.rLayout);
        percent         = (TextView) findViewById(R.id.percentTView);

        presenter = new LoadingPresenter(this);
        util = new Utils();
        watcher = new NetworkWatcher(this, layout);
        watcher.setOnInternetListener(this);



        progressC.setOnCircleProgressListener(new CircleProgress.OnCircleProgressListener() {
            @Override
            public void onProgressUpdated(int progress) {
                percent.setText(String.valueOf(progress));
            }
        });


        watcher.isNetworkAvailable();

    }

    private void downloadData() {
        if (LoadingPresenter.isStarted())
            presenter.onRestart();
        else
            presenter.startDownload();

    }

    public void downloadComplete() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startSession();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        },1200);

    }

    public void notifyProgress(int p) {
        progressC.setProgress(p);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();

    }

    @Override
    protected void onDestroy() {
        presenter.onDestroyOrBackPressed();
        removeAsynTasks();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        presenter.onDestroyOrBackPressed();
        removeAsynTasks();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(presenter.onSaveInstanceState(outState));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LoadingPresenter p = new LoadingPresenter(this);
        p.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (presenter == null){
            presenter = new LoadingPresenter(this);
        }
        presenter.onRestart();
    }

    @Override
    public void onConnected() {
        super.onConnected();
        downloadData();
    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();

    }

    public void onDownloadError(){
        if (watcher != null)
            watcher.isNetworkAvailable();
    }

    @Override
    public void onRetry() {
        super.onRetry();
        presenter.onRestart();
    }

    public void startSession() {
        if (appController != null && util != null){
            if(appController.isTokenValide()){
                startActivity(new Intent(this, MainActivity.class));
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
            util.onOpenWithAnimation(LoadingActivity.this);
            finish();

        }
    }
}

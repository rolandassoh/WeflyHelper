package com.wedevgroup.weflyhelper.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.presenter.ForceSynchroniseManager;
import com.wedevgroup.weflyhelper.service.NetworkSchedulerService;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.PermissionUtil;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.design.KenBurnsView;

import java.util.concurrent.TimeUnit;

public class SplashScreensActivity extends DBActivity {
	
	private KenBurnsView mKenBurns;
	private Utils utils;
	private ImageView mLogo;
	private final String TAG = getClass().getSimpleName();
    private PermissionUtil pUtil;
    private static boolean isAllPermissionGranted = false;
    private static boolean showPermissionDialog;
    private static boolean isFirstTime = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_splash_screen);
		
		mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo = (ImageView) findViewById(R.id.logo);
		utils = new Utils();

		mKenBurns.setImageResource(R.drawable.img_splash);
		animation2();


		pUtil = new PermissionUtil(this);
		// Listen Network change
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            scheduleJob();
        }
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
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}, TimeUnit.SECONDS.toMillis(1));
				isFirstTime = false;
			}
		}else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //check if User is already connected
                    //Read token
                    iniSetting();
                    startSession();

                }
            }, 4000);
        }

	}

    private void iniSetting() {
        if (!Save.defaultLoadBoolean(Constants.PREF_IS_FIRST_LAUNCH, SplashScreensActivity.this)){
            // First launch
            Save.defaultSaveBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL,true, SplashScreensActivity.this);
            Save.defaultSaveBoolean(Constants.PREF_AUTO_POST_SERVICE_IS_ENABLE,true, SplashScreensActivity.this);
            Save.defaultSaveBoolean(Constants.PREF_IS_LANGUAGE_FRENCH,true, SplashScreensActivity.this);
            // Force syn
            ForceSynchroniseManager f = new ForceSynchroniseManager(this);
            f.mustForceCheck();

            Save.defaultSaveBoolean(Constants.PREF_IS_FIRST_LAUNCH,true, SplashScreensActivity.this);
        }
    }


    private void animation2() {
		mLogo.setAlpha(1.0F);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
		mLogo.startAnimation(anim);
	}


    public void onRequestAllPermissions(){
        //request Permission
        pUtil.requestAllPermissions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(showPermissionDialog)
            pUtil.requestAllPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_APP_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    showPermissionDialog = false;
                    isAllPermissionGranted = true;

                    iniSetting();
                    startSession();


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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void scheduleJob() {
        try {
            JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                    .setRequiresCharging(false)
                    .setMinimumLatency(1000)
                    .setOverrideDeadline(3000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();

            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(myJob);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        // Listen Network change
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            stopService(new Intent(this, NetworkSchedulerService.class));
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
            startService(startServiceIntent);
        }

    }

    public void startSession() {
        if (appController != null && utils != null){
            if(appController.isTokenValide()){
                if (appController.isAdministrator())
                    appController.startAdminActivity(this);
                else{
                    if (Save.defaultLoadBoolean(Constants.PREF_LOADER_IS_LOADED, SplashScreensActivity.this))
                        startActivity(new Intent(this, MainActivity.class));
                    else{
                        startActivity(new Intent(SplashScreensActivity.this, LoadingActivity.class));
                    }
                }

            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
            utils.onOpenWithAnimation(this);
            finish();
        }
    }

}

package com.wedevgroup.weflyhelper.presenter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.LoadingActivity;
import com.wedevgroup.weflyhelper.activity.SplashScreensActivity;
import com.wedevgroup.weflyhelper.task.CheckUpdateTask;
import com.wedevgroup.weflyhelper.task.CultureTypesGetTask;
import com.wedevgroup.weflyhelper.task.CulturesGetTask;
import com.wedevgroup.weflyhelper.task.RegionsGetTask;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by admin on 24/04/2018.
 */

public class LoadingPresenter  {
    private static boolean isCultureLoaded = false;
    private static boolean isCultTypeLoaded = false;
    private static boolean isRegionsLoaded = false;
    private static boolean isDateLoaded = false;
    private static boolean isStateAv;
    private static boolean isStarted = false;
    private LoadingActivity act;
    private DBActivity actMain;
    String dateSaved = "";
    String dateOnServer = "";
    AppController appController;
    private final String  TAG = getClass().getSimpleName();

    public LoadingPresenter(@NonNull final LoadingActivity activity){
        this.act = activity;
        appController = AppController.getInstance();
    }

    public LoadingPresenter(@NonNull final DBActivity activity){
        this.actMain = activity;
        appController = AppController.getInstance();
    }

    public void startDownload(){
        init(true);
        getCultTypeList();
    }

    public void init(boolean canClearOld){
        try {
            Save.defaultSaveBoolean(Constants.PRESENTER_LOADER_STOP_TASK_IS_ENABLE, false, act);

            if (canClearOld){
                DataBasePresenter.getInstance().clearTypesCulture(getActivity());
                DataBasePresenter.getInstance().clearCultures(getActivity());
                DataBasePresenter.getInstance().clearRegions(getActivity());
                DataBasePresenter.getInstance().clearDbVersionDate(getActivity());

                Save.defaultSaveBoolean(Constants.PREF_LOADER_IS_LOADED,false, getActivity());

                isCultTypeLoaded     = false;
                isCultureLoaded      = false;
                isRegionsLoaded      = false;
                isDateLoaded         = false;
                isStarted            = false;

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCultTypeList(){
        if (appController != null){
            if (appController.isTokenAndUserOk(getActivity())){
                CultureTypesGetTask task = new CultureTypesGetTask(getActivity());
                task.setOnCultureTypesDownloadCompleteListener(new CultureTypesGetTask.OnCultureTypesDownloadCompleteListener() {
                    @Override
                    public void onDownloadError(@NonNull String errorMsg) {
                        isCultTypeLoaded = false;
                        //retry auto
                        try {
                            act.onDownloadError();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onDownloadSucces(@NonNull JSONArray cultTypeJArray) {
                        try {
                            act.notifyProgress(Constants.PROGRESS_CULTURE_TYPE_VALUE);
                            DataBasePresenter.getInstance().saveTypeCultureList(act, cultTypeJArray);
                            isCultTypeLoaded = true;
                            isStarted = true;
                            // start Next
                            getCulturesList();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
                task.execute();
            }
        }
    }

    private void getCulturesList(){
        if (appController != null){
            if (appController.isTokenAndUserOk(getActivity())){
                CulturesGetTask task = new CulturesGetTask(getActivity());
                task.setOnCulturesDownloadCompleteListener(new CulturesGetTask.OnCulturesDownloadCompleteListener() {
                    @Override
                    public void onDownloadError(@NonNull String errorMsg) {
                        isCultureLoaded = false;
                        //retry auto
                        try {
                            act.onDownloadError();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onDownloadSucces(@NonNull JSONArray culturesJArray) {
                        try {
                            act.notifyProgress(Constants.PROGRESS_CULTURE_VALUE);
                            DataBasePresenter.getInstance().saveCultureList(act, culturesJArray);
                            isCultureLoaded = true;

                            // startNext
                            getRegionsList();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                task.execute();
            }
        }
    }

    private void getRegionsList(){
        if (appController != null){
            if (appController.isTokenAndUserOk(getActivity())){
                RegionsGetTask task = new RegionsGetTask(getActivity());
                task.setOnRegionDownloadCompleteListener(new RegionsGetTask.OnRegionDownloadCompleteListener() {
                    @Override
                    public void onDownloadError(@NonNull String errorMsg) {
                        isRegionsLoaded = false;
                        try {
                            act.onDownloadError();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDownloadSucces(@NonNull JSONArray regionsJArray) {
                        try {
                            act.notifyProgress(Constants.PROGRESS_REGION_VALUE);
                            DataBasePresenter.getInstance().saveRegionsList(act, regionsJArray);
                            isRegionsLoaded = true;

                            getDate();
                            //ac
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
                task.execute();
            }
        }

    }

    private void getDate(){
        if (appController != null){
            if (appController.isTokenAndUserOk(getActivity())){
                CheckUpdateTask task = new CheckUpdateTask(getActivity(), false);
                task.setOnUpdateCheckCompleteListener(new CheckUpdateTask.OnUpdateCheckCompleteListener() {
                    @Override
                    public void onDownloadError(@NonNull String errorMsg) {
                        isDateLoaded = false;
                        try {
                            act.onDownloadError();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDownloadSucces(@NonNull JSONObject response) {
                        // save Date
                        try{
                            String dateCulture = "";
                            String dateRegion  = "";
                            String finalDate   = "";

                            dateCulture         = response
                                    .getString("culture");
                            dateRegion          = response
                                    .getString("region");


                            finalDate = dateCulture + dateRegion;

                            act.notifyProgress(Constants.PROGRESS_DATE_VALUE);
                            DataBasePresenter.getInstance().saveOnlineDBVersion(act, finalDate);
                            isDateLoaded = true;

                            // Do not dowload Again
                            Save.defaultSaveBoolean(Constants.PREF_LOADER_IS_LOADED,true, act);
                            act.downloadComplete();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                task.execute();
            }
        }

    }

    public void checkUpdate(){
        // get Db date
        if (actMain != null){
            try{
                dateSaved = DataBasePresenter.getInstance().getDbVersionDate(actMain);
            }catch (Exception e){
                e.printStackTrace();
            }
            // getServer date
            if (appController != null){
                if (appController.isTokenAndUserOk(getActivity())){
                    CheckUpdateTask task = new CheckUpdateTask(getActivity(), true);
                    task.setOnUpdateCheckCompleteListener(new CheckUpdateTask.OnUpdateCheckCompleteListener() {
                        @Override
                        public void onDownloadError(@NonNull String errorMsg) {
                        }

                        @Override
                        public void onDownloadSucces(@NonNull JSONObject response) {
                            try {
                                // CHECK DB version
                                String dateCulture = "";
                                String dateRegion  = "";
                                String msg         = "";
                                int versionOnServ  = 0;
                                dateCulture         = response
                                        .getString("culture");
                                dateRegion          = response
                                        .getString("region");
                                versionOnServ       = response
                                        .getInt("version");
                                msg       = response
                                        .getString("msg_helper");

                                dateOnServer = dateCulture + dateRegion;

                                if ((!dateSaved.trim().equals("")) && (!dateOnServer.trim().equals(""))){
                                    if (!dateSaved.contentEquals(dateOnServer)){
                                        // need update so clear All
                                        Save.defaultSaveBoolean(Constants.PRESENTER_LOADER_STOP_TASK_IS_ENABLE, false, actMain);

                                        DataBasePresenter.getInstance().clearTypesCulture(getActivity());
                                        DataBasePresenter.getInstance().clearCultures(getActivity());
                                        DataBasePresenter.getInstance().clearRegions(getActivity());
                                        DataBasePresenter.getInstance().clearDbVersionDate(getActivity());

                                        Save.defaultSaveBoolean(Constants.PREF_LOADER_IS_LOADED,false, actMain);

                                        isCultTypeLoaded     = false;
                                        isCultureLoaded      = false;
                                        isRegionsLoaded      = false;
                                        isDateLoaded         = false;
                                        isStarted            = false;
                                    }
                                }

                                //check app Version
                                final String appId = getActivity().getPackageName();
                                if(versionOnServ != 0 && versionOnServ > Constants.PREF_APP_VERSION){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(getActivity().getString(R.string.version_title));
                                    builder.setMessage(getActivity().getString(R.string.version_msg )+ " :" + msg);
                                    builder.setPositiveButton(getActivity().getString(R.string.update),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    // go to play Store
                                                    try {
                                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                                                                Uri.parse("market://details?id=" + appId
                                                                )));
                                                    } catch (Exception anfe) {
                                                        try {
                                                            getActivity().startActivity(new Intent(
                                                                    Intent.ACTION_VIEW,
                                                                    Uri.parse("http://play.google.com/store/apps/details?id=" +appId
                                                                    )));
                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });
                                    builder.setNegativeButton(getActivity().getString(R.string.cancel_dialog),
                                            null);
                                    builder.show();
                                }


                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    task.execute();
                }
            }
        }



    }


    public void onRestart(){
        init(false);
        Handler h = new Handler();
        try {
            Save.defaultSaveBoolean(Constants.PRESENTER_LOADER_STOP_TASK_IS_ENABLE, false, getActivity());

            act.notifyProgress(Constants.PROGRESS_DEFAULT_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isCultTypeLoaded){
                        act.notifyProgress(Constants.PROGRESS_CULTURE_TYPE_VALUE);
                        getCulturesList();
                    }else if (isCultureLoaded){
                        act.notifyProgress(Constants.PROGRESS_CULTURE_VALUE);
                        getRegionsList();
                    }else if (isRegionsLoaded){
                        act.notifyProgress(Constants.PROGRESS_REGION_VALUE);
                        getDate();

                    }else if (isDateLoaded){
                        act.notifyProgress(Constants.PROGRESS_DATE_VALUE);
                        act.downloadComplete();
                    }
                    else{
                        act.notifyProgress(Constants.PROGRESS_DEFAULT_VALUE);
                        getCultTypeList();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 1200);



    }

    public void onDestroyOrBackPressed(){
        try {
            boolean b = Save.defaultSaveBoolean(Constants.PRESENTER_LOADER_STOP_TASK_IS_ENABLE, true, act);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public Bundle onSaveInstanceState(@NonNull Bundle outState) {

        outState.putBoolean(Constants.PRESENTER_LOADER_OLD_IS_CULT_TYPE_LOADED, isCultTypeLoaded);
        outState.putBoolean(Constants.PRESENTER_LOADER_OLD_IS_CULTURE_LOADED, isCultureLoaded);
        outState.putBoolean(Constants.PRESENTER_LOADER_OLD_IS_REGION_LOADED, isRegionsLoaded);
        outState.putBoolean(Constants.PRESENTER_LOADER_OLD_IS_DATE_LOADED, isDateLoaded);
        isStateAv = true;

        return outState;
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            if (isStateAv){
                isCultTypeLoaded = savedInstanceState.getBoolean(Constants.PRESENTER_LOADER_OLD_IS_CULT_TYPE_LOADED, false);
                isCultureLoaded  = savedInstanceState.getBoolean(Constants.PRESENTER_LOADER_OLD_IS_CULTURE_LOADED, false);
                isRegionsLoaded  = savedInstanceState.getBoolean(Constants.PRESENTER_LOADER_OLD_IS_REGION_LOADED, false);
                isDateLoaded     = savedInstanceState.getBoolean(Constants.PRESENTER_LOADER_OLD_IS_DATE_LOADED, false);
            }

            onRestart();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void onStop() {
        try {
            boolean b = Save.defaultSaveBoolean(Constants.PRESENTER_LOADER_STOP_TASK_IS_ENABLE, true, act);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isStarted() {
        return isStarted;
    }

    private DBActivity getActivity(){
        if (act == null)
            return actMain;
        else
            return act;

    }

}

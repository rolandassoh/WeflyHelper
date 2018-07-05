package com.wedevgroup.weflyhelper.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.LinearLayout;

import com.wedevgroup.weflyhelper.activity.MainActivity;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.presenter.DataBasePresenter;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 02/04/2018.
 */

public class ParcelleGetAllTask extends AsyncTask<Void, Integer, Boolean> {
    LinearLayout liMain;
    private MainActivity act;
    private String TAG = getClass().getSimpleName();
    private final ArrayList<Parcelle> list = new ArrayList<>();
    private String response = "";
    private boolean withInternet;
    private AppController appController;
    private static boolean amIrunning;
    private OnParcelleLoadingCompleteListener listener;
    private boolean isServerErr;
    DataBasePresenter pDb;

    public ParcelleGetAllTask(@NonNull final MainActivity activity, boolean withInternet, @NonNull LinearLayout liMain){
        this.act = activity;
        this.withInternet = withInternet;
        this.liMain = liMain;
        appController = AppController.getInstance();
        AppController.addTask(this);
        pDb = DataBasePresenter.getInstance();
    }
    @Override
    protected void onPreExecute() {
        try {
            amIrunning = true;
            act.onDisplayUi(false, false, false, true, false, true, false);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (withInternet){
                // Get List
                GetParcellesNetworkUtilities util = new GetParcellesNetworkUtilities();
                response = util.getResponseFromHttpUrl(Constants.PARCELLES_DOWNLOAD_URL);
                if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                    if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                        // List Empty
                        return true;
                    }
                    JSONArray jArray = null;
                    jArray = new JSONArray(response);
                    pDb.synchroDatabase(jArray, act);

                }else{
                    isServerErr = true;
                }
            }

            list.clear();
            list.addAll(pDb.getParcelles());
            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        notifyOnParcelleLoadingCompleteListener(isOk, list);
        amIrunning = false;

    }


    public final class GetParcellesNetworkUtilities {
        public String getResponseFromHttpUrl(final String base_url) throws IOException {

            HttpClient httpclient ;
            HttpGet httpget = new HttpGet(base_url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);

            try {
                httpget.setHeader("Content-type", "application/json;charset=UTF-8");
                httpget.setHeader("Accept-Type","application/json");
                if (appController != null)
                    httpget.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
                response = httpclient.execute(httpget);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return Constants.SERVER_ERROR;
        }

    }

    public void setOnParcelleLoadingCompleteListener(@NonNull OnParcelleLoadingCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnParcelleLoadingCompleteListener {
        void onLoadError();
        void onSucces(@NonNull ArrayList<Parcelle> parcelles);
        void onServerError();
    }

    private void notifyOnParcelleLoadingCompleteListener(boolean isOK, @NonNull ArrayList<Parcelle> list) {
        if (listener != null){
            try {
                if (isServerErr){
                    listener.onServerError();
                    listener.onSucces(list);
                }
                else{
                    if (isOK){
                        listener.onSucces(list);
                    }else{
                        listener.onLoadError();
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static boolean imRunning(){
        return amIrunning;
    }

    public void run(){
        if (!imRunning())
            this.execute();
    }
}

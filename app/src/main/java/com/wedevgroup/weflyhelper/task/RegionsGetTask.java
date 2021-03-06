package com.wedevgroup.weflyhelper.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.AppController;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by admin on 02/04/2018.
 */

public class RegionsGetTask extends AsyncTask<Void, Integer, Boolean> {
    private DBActivity act;
    String response = "";
    private OnRegionDownloadCompleteListener listener;
    private String TAG = getClass().getSimpleName();
    private boolean isStop;
    private AppController appController;

    public RegionsGetTask(@NonNull DBActivity activity){
        this.act = activity;
        AppController.addTask(this);
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SignInTaskNetworkUtilities util = new SignInTaskNetworkUtilities();
            response = util.getResponseFromHttpUrl(Constants.REGIONS_URL);
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                    // List Empty
                    return false;
                }
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  false;
    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        try {
            isStop = Save.defaultLoadBoolean(Constants.PRESENTER_LOADER_STOP_TASK_IS_ENABLE, act);
            if (!isStop)
                notifyOnRegionDownloadCompleteListener(isOk, response);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void setOnRegionDownloadCompleteListener(@NonNull OnRegionDownloadCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnRegionDownloadCompleteListener {
        void onDownloadError(@NonNull String errorMsg);
        void onDownloadSucces(@NonNull JSONArray regionsJArray);
    }

    private void notifyOnRegionDownloadCompleteListener(boolean isOK, @NonNull String response) {
        if (listener != null){
            try {
                if (isOK){
                    JSONArray array = new JSONArray(response);
                    listener.onDownloadSucces(array);
                }else{
                    listener.onDownloadError(response);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public final class SignInTaskNetworkUtilities {
        public String getResponseFromHttpUrl( String base_url) throws IOException {

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
}

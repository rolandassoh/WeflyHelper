package com.wedevgroup.weflyhelper.task;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.wedevgroup.weflyhelper.model.Account;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.NetworkWatcher;
import com.wedevgroup.weflyhelper.utils.Save;

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

public class ProfileGetTask extends AsyncTask<Void, Integer, Boolean> {
    private BaseActivity act;
    String response = "";
    private String TAG = getClass().getSimpleName();
    private OnProfileDownloadCompleteListener listener;
    private AppController appController;
    private NetworkWatcher watcher;
    private String url = "";

    public ProfileGetTask(@NonNull BaseActivity activity){
        this.act = activity;
        AppController.addTask(this);
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        if (appController != null){
            try {
                url = Constants.USER_PROFILE + String.valueOf(appController.getUserId() +"/");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            SignInTaskNetworkUtilities util = new SignInTaskNetworkUtilities();
            response = util.getResponseFromHttpUrl(url);
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY)){
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
        notifyOnProfileDownloadCompleteListener(isOk, response);

    }

    public void setOnProfileDownloadCompleteListener(@NonNull OnProfileDownloadCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnProfileDownloadCompleteListener {
        void onDownloadError(@NonNull String errorMsg);
        void onDownloadSucces(@NonNull Account account);
        void onNetworkError();
    }

    private void notifyOnProfileDownloadCompleteListener(boolean isOK, @NonNull String response) {
        if (listener != null){
            if (isOK){
                if (appController != null)
                    listener.onDownloadSucces(appController.accountStringToObj(response));

            }else{
                listener.onDownloadError(response);
            }
            try {


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public final class SignInTaskNetworkUtilities {
        public String getResponseFromHttpUrl( @NonNull String base_url) throws IOException {

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

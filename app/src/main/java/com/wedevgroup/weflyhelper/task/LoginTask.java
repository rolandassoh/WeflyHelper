package com.wedevgroup.weflyhelper.task;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import dmax.dialog.SpotsDialog;


/**
 * Created by admin on 02/04/2018.
 */

public  class LoginTask extends AsyncTask<Void, Integer, Boolean> {
    private String response = "";
    private OnLoginListener listener;
    private View v;
    private JSONObject obj;
    private String uName, uPword;
    private BaseActivity act;
    private String token = "";
    private AlertDialog dialog;
    private boolean isServerError;

    private String TAG = getClass().getSimpleName();

    public LoginTask(@NonNull String uName, @NonNull String pWord, @NonNull final BaseActivity act){
        this.uName = uName;
        this.uPword = pWord;
        this.act = act;
        obj= new JSONObject();
    }
    @Override
    protected void onPreExecute() {
        AppController.addTask(this);
        try {
            obj.put("username", uName);
            obj.put("password", uPword);

            dialog = new SpotsDialog(act, R.style.SpotAlertDialog);
            dialog.show();
            TextView title = dialog.findViewById(R.id.dmax_spots_title);
            title.setText(act.getString(R.string.loading_connecting));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        LoginNetworkUtilities util = new LoginNetworkUtilities();
        try {
            response = util.getResponseFromHttpUrl(obj, Constants.LOGIN_URL );
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().equals(Constants.RESPONSE_EMPTY) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                // post update
                if(response.trim().contains(Constants.RESPONSE_EMPTY_INPUT)){
                    return false;
                }
                return true;
            }else
                isServerError = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        try {
            dialog.dismiss();
            if (isOk){
                token = new JSONObject(response)
                        .getString("token");
                Save.defaultSaveString(Constants.PREF_TOKEN, token, act);
                Save.defaultSaveString(Constants.PREF_USER_NAME, uName, act);
                Save.defaultSaveString(Constants.PREF_USER_PASSWORD, uPword, act);
                // Dont clear DB if is save user
                String lastUsName =Save.defaultLoadString(Constants.PREF_USER_LAST_NAME,act);
                String lastUsPwd =Save.defaultLoadString(Constants.PREF_USER_LAST_PASSWORD,act);

                if (!lastUsName.toString().trim().contentEquals(uName.toString().trim()) &&
                        !lastUsPwd.toString().trim().contentEquals(uPword.toString().trim())){
                    Save.defaultSaveString(Constants.PREF_USER_LAST_NAME, uName, act);
                    Save.defaultSaveString(Constants.PREF_USER_LAST_PASSWORD, uPword, act);
                    // Not same
                    Save.defaultSaveBoolean(Constants.PREF_IS_SAME_USER, true, act);
                }else{
                    //Same User
                    Save.defaultSaveBoolean(Constants.PREF_IS_SAME_USER, true, act);
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
        notifyOnLoginListener(isOk);
    }

    public void setOnLoginListener(@NonNull OnLoginListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;

    }


    public static interface OnLoginListener {
        void onLoginError(@NonNull View view);
        void onLoginServerError();
        void onLoginSucces();
    }

    private void notifyOnLoginListener(boolean isDone) {
        if (listener != null  && v != null){
            try {
                if (isServerError)
                    listener.onLoginServerError();
                else{
                    if (isDone)
                        listener.onLoginSucces();
                    else
                        listener.onLoginError(v);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public final class LoginNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull JSONObject jsonParam, @Nullable String url) throws IOException {

            HttpClient httpclient ;
            HttpPost httppost = new HttpPost(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);

            try {
                httppost.setEntity(new StringEntity(jsonParam.toString(), "UTF-8"));
                httppost.setHeader("Content-type", "application/json");
                response = httpclient.execute(httppost);
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

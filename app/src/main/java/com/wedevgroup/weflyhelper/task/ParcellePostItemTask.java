package com.wedevgroup.weflyhelper.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.presenter.DataBasePresenter;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.AppController;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by admin on 02/04/2018.
 */

public  class  ParcellePostItemTask extends AsyncTask<Void, Integer, Boolean> {
    final Parcelle parcelle;
    private String response = "";
    private String url = "";
    private OnParcelleSendListener listener;
    private View v;
    private final Context ctx;
    private String result = "";
    private AppController appController;
    private String TAG = getClass().getSimpleName();

    public ParcellePostItemTask(@NonNull Parcelle parcelle, @NonNull final Context ctx){
        this.parcelle = parcelle;
        this.ctx = ctx;
        AppController.addTask(this);
        appController = AppController.getInstance();
    }
    @Override
    protected void onPreExecute() {
        // update or Create

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ParcellePostItemNetworkUtilities util = new ParcellePostItemNetworkUtilities();
        try {
            if (parcelle.isNew()){
                url = Constants.PARCELLE_SENT_URL;
                try {
                    result = parcelle.parcelleToJSONObjAsPostItem().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                url = Constants.PARCELLE_SENT_UPDATE_URL;
                result = " {  ";

                result += parcelle.parcelleToStringWithServId(0, ctx);

                result += " }";
            }
            response = util.getResponseFromHttpUrl(result,
                    url);





            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().equals(Constants.RESPONSE_EMPTY) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().contains(Constants.RESPONSE_EMPTY_INPUT)){
                    return false;
                }
                JSONObject jOb = new JSONObject(response)
                        .getJSONArray("reponse")
                        .getJSONArray(0)
                        .getJSONObject(0);
                parcelle.setParcelleId(jOb.getInt("id_android"));
                parcelle.setIdOnServer(jOb.getInt("id"));
                parcelle.setDateSoumission(jOb.getString("date_soumission"));
                parcelle.setDelete(Boolean.valueOf(jOb.getString("isDelete")));
                parcelle.setNew(false);
                //parcelle.setZone(jOb.getString("zone"));


                if (DataBasePresenter.getInstance().updateParcelleFromApi(parcelle)){
                    DataBasePresenter.getInstance().close();
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        notifyOnParcelleSendListener(isOk, parcelle);
    }

    public void setOnParcelleSendListener(@NonNull OnParcelleSendListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;
    }

    public static interface OnParcelleSendListener {
        void onSendError(@NonNull Parcelle p, @NonNull View view, boolean many);
        void onSendSucces(@NonNull Parcelle p, @NonNull View view);
    }

    private void notifyOnParcelleSendListener(boolean isDone, @NonNull Parcelle parcel) {
        if (listener != null  && v != null){
            try{
                if (isDone)
                    listener.onSendSucces(parcel, v);
                else
                    listener.onSendError(parcel, v, false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    public final class ParcellePostItemNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull String obj,@NonNull String url) throws IOException {

            HttpClient httpclient ;
            HttpPost httppost = new HttpPost(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);

            try {
                httppost.setEntity(new StringEntity(obj, "UTF-8"));
                httppost.setHeader("Content-type", "application/json");
                if (appController != null)
                    httppost.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + appController.getToken());
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

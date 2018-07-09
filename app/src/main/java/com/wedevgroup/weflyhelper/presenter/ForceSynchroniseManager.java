package com.wedevgroup.weflyhelper.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.MainActivity;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 06/07/2018.
 */

public class ForceSynchroniseManager extends Activity {
    private final DBActivity act;
    private ArrayList<Parcelle> parcelles = new ArrayList<>();
    AppController appController;
    private String resPostCreate = "";
    private String resPostUpdate = "";
    private boolean isEmpty = false;
    DataBasePresenter pDb;
    private OnSynchronizationCompleteListener listener;
    private OnSynchronizationCheckListener chListener;
    private final String  TAG = getClass().getSimpleName();

    public ForceSynchroniseManager(@NonNull final DBActivity activity){
        this.act = activity;
        this.pDb = DataBasePresenter.getInstance();
        AppController.addToDestroyList(act);
        appController = AppController.getInstance();

    }


    public void shouldNotCheckAgain(){
        if (act != null){
            try {
                Save.defaultSaveBoolean(Constants.PREF_SYNCH_IS_FIRST_TIME, false, act);
                Save.defaultSaveBoolean(Constants.PREF_SYNCH_MUST_FORCE_IS_ENABLE, false, act);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void mustForceCheck(){
        if (act != null){
            try {
                Save.defaultSaveBoolean(Constants.PREF_SYNCH_MUST_FORCE_IS_ENABLE, true, act);
                Save.defaultSaveBoolean(Constants.PREF_SYNCH_IS_FIRST_TIME, true, act);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean imNeedSyn(){
        if (act != null){
            try {
                boolean isFistTime = Save.defaultLoadBoolean(Constants.PREF_SYNCH_IS_FIRST_TIME, act);

                if (isFistTime){
                    // Check shared for last Activity
                    boolean mustForce = Save.defaultLoadBoolean(Constants.PREF_SYNCH_MUST_FORCE_IS_ENABLE, act);

                    if (mustForce){
                        // Show Dialog on Main
                        if (act instanceof MainActivity){
                            final MainActivity activity = (MainActivity) act;

                            AlertDialog.Builder builder = new AlertDialog.Builder(act);
                            builder.setTitle(act.getString(R.string.synch_ttle));
                            builder.setMessage(act.getString(R.string.synch_msg));
                            builder.setPositiveButton(act.getString(R.string.yes),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            try {
                                                activity.resquestForceSyn();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            builder.setNegativeButton(act.getString(R.string.cancel_dialog),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Exit from application
                                            exist();

                                        }
                                    });
                            builder.show();
                        }else{
                            // Close on CreateActivity
                            act.finish();
                        }
                    }else
                        return false;


                }else {
                    // Not first Time : already check
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return true;
    }

    private void exist() {
        Utils utils = new Utils();
        mustForceCheck();
        AppController.clearDestroyList();
        AppController.clearAsynTask();
        if (act != null)
            utils.onFinishWithAnimation(act);
    }

    public void startSynchro(){
        if (act != null && pDb != null){
            try {
                boolean isFistTime = Save.defaultLoadBoolean(Constants.PREF_SYNCH_IS_FIRST_TIME, act);

                // stop service tempo
                Save.defaultSaveBoolean(Constants.THREAD_SYNCHRO_FORCED_IS_RUNNING,true, act);


                if (isFistTime){
                    new AsyncTask<Void, Integer, Boolean>(){

                        @Override
                        protected void onPreExecute() {
                            AppController.addTask(this);
                            if (act instanceof MainActivity){
                                final MainActivity activity = (MainActivity) act;
                                activity.onDisplayUi(false, false, false, true, false, false, false);
                            }
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {

                            CopyOnWriteArrayList<Parcelle> collection = new CopyOnWriteArrayList<Parcelle>();
                            CopyOnWriteArrayList<Parcelle> listToCreate = new CopyOnWriteArrayList<Parcelle>();
                            boolean isCreateResOk = false;
                            boolean isUpdateResOk = false;
                            parcelles.addAll(pDb.getParcelles());

                            if (parcelles.size() > 0){
                                for (Parcelle dm: parcelles){
                                    if (dm.getDateSoumission().toString().trim().equals(""))
                                        collection.add(dm);
                                }

                                // have updated
                                if (collection.size() > 0){
                                    // send Data
                                    for (Parcelle cDm: collection){
                                        if (cDm.isNew()){
                                            listToCreate.add(cDm);
                                            collection.remove(cDm);
                                        }

                                    }

                                    // Send all New
                                    if (listToCreate.size() > 0){
                                        ParcelleServiceNetworkUtilities post = new ParcelleServiceNetworkUtilities();
                                        try {
                                            resPostCreate = post.getResponseFromHttpUrl(convertListToString(listToCreate, false, act), Constants.PARCELLE_SENT_URL);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (!resPostCreate.trim().equals("") && !resPostCreate.trim().equals(Constants.SERVER_ERROR) && !resPostCreate.trim().equals(Constants.RESPONSE_EMPTY) && !resPostCreate.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                                            if(resPostCreate.trim().contains(Constants.RESPONSE_EMPTY_INPUT)){
                                                isCreateResOk = false;
                                            }else{
                                                // post update
                                                try {
                                                    JSONArray jArray = new JSONObject(resPostCreate)
                                                            .getJSONArray("reponse")
                                                            .getJSONArray(0);
                                                    // Save All new
                                                    isCreateResOk = true;

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        }

                                    }


                                    // Send all to Update
                                    if (collection.size() > 0){

                                        ParcelleServiceNetworkUtilities update = new ParcelleServiceNetworkUtilities();
                                        try {
                                            resPostUpdate = update.getResponseFromHttpUrl(convertListToString(collection, true, act), Constants.PARCELLE_SENT_UPDATE_URL);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (!resPostUpdate.trim().equals("") && !resPostUpdate.trim().equals(Constants.SERVER_ERROR) && !resPostUpdate.trim().equals(Constants.RESPONSE_EMPTY) && !resPostUpdate.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                                            // everything is Ok so fusion
                                            if(resPostUpdate.trim().contains(Constants.RESPONSE_EMPTY_INPUT)){
                                                isUpdateResOk = false;
                                            }else {
                                                try {
                                                    JSONArray arrUpdated = new JSONObject(resPostUpdate)
                                                            .getJSONArray("reponse")
                                                            .getJSONArray(0);

                                                    //  update all
                                                    isUpdateResOk = true;

                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                    }

                                    // Save All
                                    if (isCreateResOk || isUpdateResOk){

                                        // Send Done now Synchron db
                                        return synchFromServer();

                                    }else{
                                        // an error happen
                                    }

                                }else{
                                    // Db empty must not occur
                                    return synchFromServer();
                                }

                            }else{
                                // Db empty so Synchro
                                return synchFromServer();
                            }

                            return false;
                        }

                        @Override
                        protected void onPostExecute(Boolean isOk) {
                            if(isCancelled())
                                return;
                            try {
                                if (isOk){
                                    shouldNotCheckAgain();
                                }else{
                                    mustForceCheck();
                                }
                                // Allow bg post Service
                                Save.defaultSaveBoolean(Constants.THREAD_SYNCHRO_FORCED_IS_RUNNING,false, act);
                                notifyOnSynchronizationCompleteListener(isOk,parcelles);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }


                    }.execute();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private boolean synchFromServer() {
        String response = "";
        try {
            GetParcellesNetworkUtilities util = new GetParcellesNetworkUtilities();
            response = util.getResponseFromHttpUrl(Constants.PARCELLES_DOWNLOAD_URL);
            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                    // List Empty
                    // No not check again
                    return true;
                }
                JSONArray jArray = null;
                jArray = new JSONArray(response);
                pDb.synchroDatabase(jArray, act);

                parcelles.clear();
                parcelles.addAll(pDb.getParcelles());
                // No not check again
                return true;

            }else{
                // an error happen
            }
        }catch (Exception en){
            en.printStackTrace();
            // an error happen
        }

        return false;
    }

    public void showDialError() {
        if (act != null){
            // Show Dialog on Main
            if (act instanceof MainActivity){
                final MainActivity activity = (MainActivity) act;

                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle(act.getString(R.string.synch_err_ttle));
                builder.setMessage(act.getString(R.string.synch_err_msg));
                builder.setPositiveButton(act.getString(R.string.retry),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // remove all points
                                try {
                                    activity.resquestForceSyn();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.setNegativeButton(act.getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Exit from application
                                exist();
                            }
                        });
                builder.show();
            }else{
                // Close on CreateActivity
                act.finish();
            }
        }

    }

    public static interface OnSynchronizationCompleteListener {
        void onSynError();
        void onSynSucces(@NonNull ArrayList<Parcelle> parcelles);
    }

    public void setOnSynchronizationCompleteListener(@NonNull OnSynchronizationCompleteListener listener) {
        this.listener = listener;

    }

    private void notifyOnSynchronizationCompleteListener (boolean isOK, @NonNull ArrayList<Parcelle> list) throws Exception {
        if (listener != null){
            if (isOK){
                listener.onSynSucces(list);
            }else{
                listener.onSynError();
            }
        }
    }

    protected @NonNull String convertListToString(@NonNull CopyOnWriteArrayList<Parcelle> list, boolean forUpdate, @NonNull final Context ctx){
        String resul = " {  ";

        for (int i = 0; i < list.size() - 1; i++){
            if (forUpdate)
                resul += list.get(i).parcelleToStringWithServId(i, ctx) + " , ";
            else
                resul += list.get(i).parcelleToString(i, ctx) + " , ";


        }
        if (list.size() > 0){
            if (forUpdate)
                resul += list.get(list.size()  - 1).parcelleToStringWithServId(list.size() - 1, ctx);
            else
                resul += list.get(list.size()  - 1).parcelleToString(list.size() - 1, ctx);
        }

        resul += " }";


        return resul;

    }

    public final class ParcelleServiceNetworkUtilities {
        public String getResponseFromHttpUrl(@NonNull String data, @NonNull String url) throws IOException {

            HttpClient httpclient ;
            HttpPost httppost = new HttpPost(url);
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Constants.VOLLEY_TIME_OUT);
            httpclient = new DefaultHttpClient(httpParameters);

            try {
                httppost.setEntity(new StringEntity(data, "UTF-8"));
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

    public void checkForceSyn(){
        if (act != null){

            try {
                // stop service tempo
                Save.defaultSaveBoolean(Constants.THREAD_SYNCHRO_FORCED_IS_RUNNING,true, act);

                new AsyncTask<Void, Integer, Boolean>(){
                    @Override
                    protected void onPreExecute() {
                        AppController.addTask(this);
                        if (act instanceof MainActivity){
                            final MainActivity activity = (MainActivity) act;
                            activity.onDisplayUi(false, false, false, true, false, false, false);
                        }

                    }


                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        String response = "";
                        try {
                            GetParcellesNetworkUtilities util = new GetParcellesNetworkUtilities();
                            response = util.getResponseFromHttpUrl(Constants.PARCELLES_DOWNLOAD_URL);
                            if (!response.trim().equals("") && !response.trim().equals(Constants.SERVER_ERROR) && !response.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                                if(response.trim().equals(Constants.RESPONSE_EMPTY) || response.trim().equals(Constants.RESPONSE_EMPTY_OTHER)){
                                    // List Empty
                                    // No not check again
                                    isEmpty = true;
                                }
                                JSONArray jArray = null;
                                jArray = new JSONArray(response);

                                if (jArray.length() > 0){
                                    // No not check again
                                    return true;
                                }


                            }else{
                                // an error happen
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean isOk) {
                        if(isCancelled())
                            return;
                        try {
                            notifyOnSynchronizationCheckListener(isOk);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    public static interface OnSynchronizationCheckListener {
        void onCheckError();
        void onCheckSucces();
        void onCheckEmpty();
    }

    public void setOnSynchronizationCheckListener(@NonNull OnSynchronizationCheckListener listener) {
        this.chListener = listener;
    }

    private void notifyOnSynchronizationCheckListener (boolean isOK) throws Exception {
        if (chListener != null) {
            if (isEmpty){
                chListener.onCheckEmpty();
            }else{
                if (isOK){
                    chListener.onCheckSucces();
                }else{
                    chListener.onCheckError();
                }
            }

        }
    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}

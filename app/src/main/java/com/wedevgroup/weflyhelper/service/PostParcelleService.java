package com.wedevgroup.weflyhelper.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.MainActivity;
import com.wedevgroup.weflyhelper.model.MessageEB;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.presenter.DBService;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Obrina.KIMI on 1/16/2018.
 */

public class PostParcelleService extends DBService {
    public Thread thread;
    private int serviceId;
    ScheduledExecutorService scheduler;
    boolean isOneSend = false;
    private final static String TAG = "PostParcelleService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(PostParcelleService.this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // service On start
        serviceId = startId;
        scheduler =
                Executors.newSingleThreadScheduledExecutor();

        try {
            isOneSend = intent.getBooleanExtra("isOneSend", false);
        }catch (Exception e){
            e.printStackTrace();
        }

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        // send time change Request
                        // check Token
                        if (isTokenAndUserOk()){
                            thread = new Thread(new mThreadClass(serviceId, PostParcelleService.this, isOneSend));
                            thread.start();
                        }

                    }
                }, 0, 1, TimeUnit.MINUTES);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // service finished
        if(thread != null){
           onStopThread(this);
        }
        super.onDestroy();
    }

    final class mThreadClass implements Runnable{
        private int serviceId;
        private boolean isOneSend;
        private Context context;
        private String resPostCreate = "";
        private String resPostUpdate = "";


        public mThreadClass(int serviceId, final Context ctx, boolean isOneSend ){
            this.serviceId = serviceId;
            this.context = ctx;
            this.isOneSend = isOneSend;
        }
        @Override
        public synchronized void run() {
            if (isOneSend)
                postOneTime();
            else
            {
                try {
                    // Can start
                    if (Save.defaultLoadBoolean(Constants.PREF_AUTO_POST_SERVICE_IS_ENABLE, getBaseContext())){
                        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isAvailable()) {

                            // No thread running
                            if(!Save.defaultLoadBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING,   getBaseContext())){
                                // start Flags
                                Save.defaultSaveBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING, true,  getBaseContext());


                                // Permission
                                if (isAllPermissionGranted){
                                    // Ping
                                    if(Utils.isConnectedToInternet(PostParcelleService.this)){
                                        // Internet Available
                                        ArrayList<Parcelle> parcelles = new ArrayList<>();
                                        CopyOnWriteArrayList<Parcelle> collection = new CopyOnWriteArrayList<Parcelle>();
                                        CopyOnWriteArrayList<Parcelle> listToCreate = new CopyOnWriteArrayList<Parcelle>();
                                        boolean isCreateResOk = false;
                                        boolean isUpdateResOk = false;

                                        parcelles.addAll(getDBManager().getParcelles());

                                        if (parcelles.size() > 0){
                                            for (Parcelle dm: parcelles){
                                                if (dm.getDateSoumission().toString().trim().equals(""))
                                                    collection.add(dm);
                                            }
                                        }else
                                            stopSelf(serviceId);


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
                                                    resPostCreate = post.getResponseFromHttpUrl(convertListToString(listToCreate, false, getBaseContext()), Constants.PARCELLE_SENT_URL);
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
                                                            isCreateResOk = saveOnDb(listToCreate, resPostCreate, getBaseContext());

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
                                                    resPostUpdate = update.getResponseFromHttpUrl(convertListToString(collection, true, getBaseContext()), Constants.PARCELLE_SENT_UPDATE_URL);
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
                                                            isUpdateResOk = updateDb(collection,resPostUpdate, getBaseContext());

                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                    }


                                                }
                                            }

                                            // Save All
                                            if (isCreateResOk || isUpdateResOk){
                                                // Notify
                                                NotificationCompat.Builder builder =
                                                        new NotificationCompat.Builder( getBaseContext())
                                                                .setSmallIcon(R.drawable.ic_notification_default)
                                                                .setContentTitle( getBaseContext().getString(R.string.notif_service_title))
                                                                .setContentText( getBaseContext().getString(R.string.succes_parcelles))
                                                                .setAutoCancel(true);

                                                Intent targetIntent = new Intent( getBaseContext(), MainActivity.class);
                                                PendingIntent contentIntent = PendingIntent.getActivity( getBaseContext(), 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                builder.setContentIntent(contentIntent);
                                                NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                nManager.notify(Constants.NOTIFICATION_SERVICE_AUTO_POST_ID, builder.build());

                                                // stop Thread
                                                stopSelf(serviceId);

                                            }else
                                                onResponseError(Constants.RESPONSE_CODE_ERROR_UPLOAD, true);

                                        }
                                    }else {
                                        onResponseError(Constants.RESPONSE_CODE_ERROR_NO_INTERNET, true);
                                    }
                                }else
                                    stopSelf();


                            }else
                                stopSelf();

                        } else {
                            stopSelf();
                        }
                    }else {
                        stopSelf();
                    }

                }catch (Exception e){
                    stopSelf();
                }
            }

        }

        private void postOneTime() {
            try {
                // Can start
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isAvailable()) {

                    // No thread running
                    if(!Save.defaultLoadBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING, context)){
                        // start Flags
                        Save.defaultSaveBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING, true, context);


                        // Permission
                        if (isAllPermissionGranted){
                            // Ping
                            if(Utils.isConnectedToInternet(PostParcelleService.this)){
                                // Internet Available
                                ArrayList<Parcelle> parcelles = new ArrayList<>();
                                CopyOnWriteArrayList<Parcelle> collection = new CopyOnWriteArrayList<Parcelle>();
                                CopyOnWriteArrayList<Parcelle> listToCreate = new CopyOnWriteArrayList<Parcelle>();
                                boolean isCreateResOk = false;
                                boolean isUpdateResOk = false;

                                parcelles.addAll(getDBManager().getParcelles());

                                if (parcelles.size() > 0){
                                    for (Parcelle dm: parcelles){
                                        if (dm.getDateSoumission().toString().trim().equals(""))
                                            collection.add(dm);
                                    }
                                }else
                                    stopSelf(serviceId);

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
                                            resPostCreate = post.getResponseFromHttpUrl(convertListToString(listToCreate, false, context), Constants.PARCELLE_SENT_URL);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (!resPostCreate.trim().equals("") && !resPostCreate.trim().equals(Constants.SERVER_ERROR) && !resPostCreate.trim().equals(Constants.RESPONSE_EMPTY) && !resPostCreate.trim().contains(Constants.RESPONSE_ERROR_HTML)){
                                            // post update
                                            if(resPostCreate.trim().contains(Constants.RESPONSE_EMPTY_INPUT)){
                                                isCreateResOk = false;
                                            }else {
                                                try {
                                                    JSONArray jArray = new JSONObject(resPostCreate)
                                                            .getJSONArray("reponse")
                                                            .getJSONArray(0);
                                                    isCreateResOk = saveOnDb(listToCreate, resPostCreate, getBaseContext());
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
                                            resPostUpdate = update.getResponseFromHttpUrl(convertListToString(collection, true, context), Constants.PARCELLE_SENT_UPDATE_URL);
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
                                                    isUpdateResOk = updateDb(collection,resPostUpdate, getBaseContext());
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                    }

                                    // Save All
                                    if (isCreateResOk || isUpdateResOk){
                                        // Notify
                                        MessageEB m = new MessageEB();
                                        m.setClassSender(PostParcelleService.class+"");
                                        m.setMsg(Constants.RESPONSE_CODE_UPLOAD_OK);

                                        EventBus.getDefault().post(m);

                                        // stop Thread
                                        stopSelf(serviceId);

                                    }else
                                        onResponseError(Constants.RESPONSE_CODE_ERROR_UPLOAD, false);
                                }
                            }else {
                                onResponseError(Constants.RESPONSE_CODE_ERROR_NO_INTERNET, false);
                            }
                        }else
                            stopSelf();

                    }else
                        stopSelf();

                } else {
                    stopSelf();
                }

            }catch (Exception e){
                stopSelf();
            }

        }

        private void onResponseError(@NonNull String code, boolean retry) {
            // Notify
            MessageEB m = new MessageEB();
            m.setClassSender(PostParcelleService.class+"");
            m.setMsg(code);

            EventBus.getDefault().post(m);
            if (retry)
                retryAgain(context);
            else {
                stopSelf(serviceId);
            }

        }
    }

    private void retryAgain(@NonNull Context ctx) {
        try {
            Save.defaultSaveBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING, false, ctx);
        }catch (Exception e){
            e.printStackTrace();
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
            Log.v(Constants.APP_NAME, TAG + "Url " + url + " List " + data  );

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
                httppost.setHeader("Authorization",Constants.TOKEN_HEADER_NAME + getToken());
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

    public void onStopThread(final Context ctx){
        //Stop Auto refresh
        if(scheduler != null){
            scheduler.shutdown();
            scheduler = null;
        }

        //Event bus unregister
        try {
            EventBus.getDefault().unregister((PostParcelleService.this));
        }catch (Exception e){
            e.printStackTrace();
        }
        //en flag
        try{
            Save.defaultSaveBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING, false, ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(MessageEB mMsg){
        if(mMsg.getClassSender().equalsIgnoreCase(PostParcelleService.class+""))
            return;

        if (mMsg.getClassSender().equalsIgnoreCase(MainActivity.class+"") && mMsg.getMsg().contentEquals(Constants.RESPONSE_STOP_SERVICE)){
            onStopThread(this);
        }



    }

}

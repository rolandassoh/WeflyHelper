package com.wedevgroup.weflyhelper.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapCustom;
import com.google.android.gms.maps.PointCustom;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.LoginActivity;
import com.wedevgroup.weflyhelper.model.Account;
import com.wedevgroup.weflyhelper.model.Point;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Obrina.KIMI on 6/21/2017.
 */
public class AppController extends Application {
    public static final String TAG = AppController.class
        .getSimpleName();

    private static AppController mInstance;
    private static ArrayList<Activity> activitiesList = new ArrayList<>();
    private static ArrayList<AsyncTask<Void, Integer, Boolean>> tasksList = new ArrayList<>();
    private static Context AppContext;
    private PointCustom pUser;
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface canaroExtraBold;
    LocalizationApplicationDelegate localizationDelegate = new LocalizationApplicationDelegate(this);

    //traffic
    private String token = "";


    public static void addToDestroyList(final Activity activity){
        try {
            activitiesList.add(activity);
        }catch (Exception e){
            e.printStackTrace();

        }
    }
    public static void addTask(@NonNull final AsyncTask<Void, Integer, Boolean> task){
        tasksList.add(task);
    }
    public static void clearDestroyList(){
        try {
            boolean isChanging = Save.defaultLoadBoolean(Constants.LANGUAGE_IS_CHANGING, getInstance());
            if (!isChanging){
                if (activitiesList != null && activitiesList.size() >0){
                    for(Activity act: activitiesList){
                        if(!act.isFinishing())
                            act.finish();
                    }
                    activitiesList.clear();
                }
            }else {
                Save.defaultSaveBoolean(Constants.LANGUAGE_IS_CHANGING, false, getInstance());

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void clearAsynTask(){
        if(tasksList != null && tasksList.size() > 0){
            for(AsyncTask<Void, Integer, Boolean> mTask: tasksList){
                if(!(null == mTask))
                {
                    mTask.cancel(true);
                }
            }
            tasksList.clear();
        }
    }


    public int getUserCoucheId() {
        int layer = 0;
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("couche");
                layer = claim.asInt();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return layer;

    }

    public boolean isAdministrator(){
        return  getUserCoucheId() == 0;
    }

    public boolean isTokenAndUserOk(@NonNull  final Context ctx){
        if (getInstance() != null){
            if (isTokenValide()){
                if (isAdministrator()){
                    startAdminActivity(ctx);
                    return false;
                }

            }else{
                startLoginActivity(ctx);
                return false;
            }


        }
        return true;
    }

    public String getUserCouche() {
        String layer  = "";
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("couche_nom");
                layer = claim.asString();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return layer;

    }

    public int getUserId() {
        int id = 0;
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("user_id");
                id = claim.asInt();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return id;

    }

    public @NonNull String getUserEmail() {
        String email = "";
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("email");
                email = claim.asString();

                if (email != null)
                    return email;
                else
                    return "";
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return email;

    }

    public @NonNull String getUserName() {
        String uName = "";
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("username");
                uName = claim.asString();

                if (uName != null)
                    return uName;
                else
                    return "";
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return uName;

    }

    public int getUserEnterId() {
        int enterprise = 0;
        if(this.isTokenValide()){
            try {
                JWT jwt = new JWT(getToken());
                Claim claim = jwt.getClaim("entreprise");
                enterprise = claim.asInt();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return enterprise;

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }


    public void cleanToken(@NonNull final  Context ctx){
        try {
            Save.defaultSaveString(Constants.PREF_TOKEN, "", ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static Context getAppContext() {
        return AppContext;
    }





    public static synchronized AppController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        AppContext = getApplicationContext();

        // Init icons
        Iconics.init(getApplicationContext());
        Iconics.registerFont(new FontAwesome());

        //FIXE CAMERA Bug on Api 24+
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Hambuger Frame font
        canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }

    // Need to test
    public boolean isTokenValide(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getApplicationContext());
            if(!token.equals("")){
                JWT jwt = new JWT(token);
                boolean isExpired = jwt.isExpired(0);
                return !isExpired;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    public void cameraOnUser(@NonNull Double lat, @NonNull Double lng, @NonNull final GoogleMapCustom map){
        LatLng pos = new LatLng(lat, lng);
        try {
            if (pUser != null && pUser.getMarker() != null){
                pUser.getMarker().remove();
            }

            pUser = map.addMarker(new MarkerOptions()
                    .position(pos)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_point_guide)),
                    new CopyOnWriteArrayList<PointCustom>(),
                    new PointCustom());
            CameraPosition BONDI = new CameraPosition.Builder().target(pos)
                    .zoom(16.5f)
                    .bearing(0)
                    .tilt(0)
                    .build();

            changeCamera(CameraUpdateFactory.newCameraPosition(BONDI),null, map);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void changeCamera(CameraUpdate update, GoogleMapCustom.CancelableCallback callback, @NonNull final GoogleMapCustom map) {
        int duration = 2000;
        // The duration must be strictly positive so we make it at least 1.
        map.animateCamera(update, Math.max(duration, 1), callback);
    }



    public  Account accountStringToObj(@NonNull String response){
        Account ac = new Account();
        try {

            JSONObject obj = new JSONObject(response);

            ac.setId(obj.getInt("id"));
            ac.setUserName(obj.getString("username"));
            ac.setEmail(obj.getString("email"));
            ac.setLastName(obj.getString("last_name"));
            ac.setFirstName(obj.getString("first_name"));
            ac.setPassword(obj.getString("password"));

            JSONObject profileObj = obj.getJSONObject("profile");

            ac.getProfile().setIdOnServer(profileObj.getInt("id"));
            ac.getProfile().setPhone(profileObj.getString("numero"));
            ac.getProfile().setImage(profileObj.getString("profil_photo"));
            ac.getProfile().setEntreprise(profileObj.getString("entreprise"));
            ac.getProfile().setFonction(profileObj.getString("nom_fonction"));
            ac.getProfile().setNumCnps(profileObj.getString("numcnps"));
            ac.getProfile().setActor(profileObj.getBoolean("a_acteur"));
            ac.setCouche(obj.getJSONArray("acteurs")
                    .getJSONObject(0)
                    .getString("couche"));



        } catch (Exception e){
            e.printStackTrace();
        }


        return ac;

    }

    public @Nullable JSONObject accountStringToJSON(@NonNull String account ){

        JSONObject obj = null;

        try {
            obj = new JSONObject(account);
        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;

    }

    public void startAdminActivity(@NonNull final Context ctx){
        Intent intent = new Intent(ctx, LoginActivity.class);
        intent.putExtra("isAdmin", true);
        startActivity(intent);
    }

    public void startLoginActivity(@NonNull final Context ctx){
        Intent intent = new Intent(ctx, LoginActivity.class);
        startActivity(intent);
    }


    // Multi language Language
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localizationDelegate.onConfigurationChanged(this);
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }


}
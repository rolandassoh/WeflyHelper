package com.wedevgroup.weflyhelper.presenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.PermissionUtil;
import com.wedevgroup.weflyhelper.utils.Save;

/**
 * Created by admin on 04/04/2018.
 */

public class BaseService extends Service {
    public static Double uLatitude = Constants.DOUBLE_NULL;
    public static Double uLongitude = Constants.DOUBLE_NULL;
    protected static boolean isAllPermissionGranted = false;
    protected PermissionUtil pUtil;
    String token = "";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pUtil = new PermissionUtil(this);
        //check permission
        isAllPermissionGranted = pUtil.isAllPermissionsGranded();
    }

    public boolean isTokenAndUserOk(){
        if (isTokenValide()){
            if (isAdministrator()){
                return false;
            }

        }else{
            return false;
        }
        return true;
    }

    public boolean isTokenValide(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getBaseContext());
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

    public String getToken(){
        try {
            token = Save.defaultLoadString(Constants.PREF_TOKEN, getBaseContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public boolean isAdministrator(){
        return  getUserCoucheId() == 0;
    }
}

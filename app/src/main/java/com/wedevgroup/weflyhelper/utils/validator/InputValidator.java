package com.wedevgroup.weflyhelper.utils.validator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.wedevgroup.weflyhelper.R;

import java.util.regex.Pattern;

/**
 * Created by admin on 13/04/2018.
 */

public class InputValidator {

    public static @NonNull String isNameValid(Context ctx, @NonNull String name){
        String alertMsg = "";
        if(name.equals("")){
            alertMsg = ctx.getString(R.string.empty_name);
            return alertMsg;
        }else{
            return "";
        }

    }

    public static @NonNull String isCoucheValid(Context ctx, @NonNull String couche){
        String alertMsg = "";
        if(couche.equals("")){
            alertMsg = ctx.getString(R.string.empty_couche);
            return alertMsg;
        }else{
            return "";
        }

    }

    public static @NonNull String isRegionValid(Context ctx, @NonNull String region){
        String alertMsg = "";
        if(region.equals("")){
            alertMsg = ctx.getString(R.string.empty_region);
            return alertMsg;
        }else{
            return "";
        }

    }

    public static @NonNull String isZone(Context ctx, @NonNull String zone){
        String alertMsg = "";
        if(zone.equals("")){
            alertMsg = ctx.getString(R.string.empty_zone);
            return alertMsg;
        }else{
            return "";
        }

    }


}

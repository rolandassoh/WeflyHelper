package com.wedevgroup.weflyhelper.utils.validator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.wedevgroup.weflyhelper.R;

import java.util.regex.Pattern;

/**
 * Created by admin on 13/04/2018.
 */

public class EmailValidator {
    public static @NonNull
    String isEmailValid(Context ctx, @NonNull String email){
        String alertMsg = "";
        if(email.equals("")){
            alertMsg = ctx.getString(R.string.empty_email);
            return alertMsg;
        }else{
            boolean isEmailValid;
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            isEmailValid = pattern.matcher(email).matches();

            if (!isEmailValid){
                alertMsg = ctx.getString(R.string.email_not_valid);
                return alertMsg;
            }
            return "";
        }

    }
}

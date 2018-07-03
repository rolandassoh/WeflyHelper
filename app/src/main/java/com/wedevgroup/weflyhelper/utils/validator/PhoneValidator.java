package com.wedevgroup.weflyhelper.utils.validator;

import android.content.Context;
import android.support.annotation.NonNull;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.utils.Utils;


/**
 * Created by Obrina.KIMI on 11/9/2017.
 */

public class PhoneValidator {
    public static @NonNull String isPhoneValid(Context ctx, @NonNull String phone)
    {
        String alertMsg = "";
        if(phone.equals("")){
            alertMsg = ctx.getString(R.string.empty_phone);
            return alertMsg;
        }else{
            boolean isValid = Utils.isPhoneNumberValid(phone);
            if(!(phone.substring(1).matches("[0-9]+")) || !phone.contains("+") || !isValid){
                alertMsg = ctx.getString(R.string.phone_not_valid);
                return alertMsg;
            }else
                return "";
        }
    }


}

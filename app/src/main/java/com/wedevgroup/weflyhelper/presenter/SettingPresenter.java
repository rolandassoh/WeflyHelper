package com.wedevgroup.weflyhelper.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;

import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;

import java.util.Locale;

/**
 * Created by admin on 21/06/2018.
 */

public class SettingPresenter {
    private final Activity act;
    OnSettingChangeListener listener;

    public SettingPresenter(@NonNull final Activity act){
        this.act = act;
    }

    public static interface OnSettingChangeListener {
        void onLanguageChanged(@NonNull String newLang);
    }

    public void setOnSettingChangeListener(@NonNull OnSettingChangeListener listener) {
        this.listener = listener;
    }

    public void setLanguage(@NonNull String newLang){
        try {
            if (newLang.equals(Constants.LANGUAGE_FRENCH)){
                Save.defaultSaveBoolean(Constants.PREF_IS_LANGUAGE_FRENCH,true, act);

                notifyOnSettingChangeListener(true, Constants.LANGUAGE_FRENCH);
            }else{
                Save.defaultSaveBoolean(Constants.PREF_IS_LANGUAGE_FRENCH,false, act);

                notifyOnSettingChangeListener(true, Constants.LANGUAGE_ENGLISH);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getLanguage(){
        String lang = "";
        try {
            boolean isFrench = Save.defaultLoadBoolean(Constants.PREF_IS_LANGUAGE_FRENCH, act);

            if (isFrench)
                lang = Constants.LANGUAGE_FRENCH;
            else
                lang = Constants.LANGUAGE_ENGLISH;
        }catch (Exception e){
            e.printStackTrace();
        }
        return lang;
    }

    public boolean isLanguageFrench(){
        String lang = "";
        try {
            boolean isFrench = Save.defaultLoadBoolean(Constants.PREF_IS_LANGUAGE_FRENCH, act);

            return isFrench;
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void notifyOnSettingChangeListener(boolean isNewLang, @NonNull String newLang) {
        if (listener != null){
            try {
                if (isNewLang){
                    listener.onLanguageChanged(newLang);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}

package com.wedevgroup.weflyhelper.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate;
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener;
import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;

import java.util.Locale;

/**
 * Created by admin on 25/06/2018.
 */

public class LanguagePresenter extends AppCompatActivity implements SettingPresenter.OnSettingChangeListener, OnLocaleChangedListener {

    String langCode = "";
    SettingPresenter langManager;
    private String TAG = getClass().getSimpleName();
    private LocalizationActivityDelegate localizationDelegate = new LocalizationActivityDelegate(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        localizationDelegate.addOnLocaleChangedListener(this);
        localizationDelegate.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);


        langManager = new SettingPresenter(this);
        langCode = langManager.getLanguage(); //load it from SharedPref

        // Use language
        setLanguage(langCode);
    }

    @Override
    public void onLanguageChanged(@NonNull String newLang) {
        // Fixed auto exit
        try {
            Save.defaultSaveBoolean(Constants.LANGUAGE_IS_CHANGING, true, this);
        }catch (Exception e){
            e.printStackTrace();
        }

        setLanguage(langCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        localizationDelegate.onResume(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(newBase));
    }

    @Override
    public Context getApplicationContext() {
        return localizationDelegate.getApplicationContext(super.getApplicationContext());
    }

    @Override
    public Resources getResources() {
        return localizationDelegate.getResources(super.getResources());
    }

    public final void setLanguage(String language) {
        localizationDelegate.setLanguage(this, language);
    }

    public final void setLanguage(Locale locale) {
        localizationDelegate.setLanguage(this, locale);
    }

    public final void setDefaultLanguage(String language) {
        localizationDelegate.setDefaultLanguage(language);
    }

    public final void setDefaultLanguage(Locale locale) {
        localizationDelegate.setDefaultLanguage(locale);
    }

    public final Locale getCurrentLanguage() {
        return localizationDelegate.getLanguage(this);
    }

    // Just override method locale change event
    @Override
    public void onBeforeLocaleChanged() {
    }

    @Override
    public void onAfterLocaleChanged() {
    }
}

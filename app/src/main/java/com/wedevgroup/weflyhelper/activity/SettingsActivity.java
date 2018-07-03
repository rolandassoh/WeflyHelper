package com.wedevgroup.weflyhelper.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageButton;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;
import com.wedevgroup.weflyhelper.presenter.SettingPresenter;
import com.wedevgroup.weflyhelper.utils.CacheImage;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.AppController;

 public class SettingsActivity extends BaseActivity implements  CacheImage.OnMovingCompleteListener, SettingPresenter.OnSettingChangeListener {
    private  Toolbar toolbar;
    private Switch scAutoPost, scStorage, scFrench, scEng;
    private RelativeLayout rLayout;
    private TextView storage;
    CacheImage c;
    private boolean isInternalPrefered;
    private LinearLayout liAbout;
    private IconicsImageButton cleanPressed;
    boolean doNotMove = false;
    boolean doNothingLang = false;
    private SettingPresenter pLang;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        toolbar              = (Toolbar)            findViewById(R.id.toolbar);

        scAutoPost           = (Switch)             findViewById(R.id.autoSendSwitch);
        scStorage            = (Switch)             findViewById(R.id.storageSwitch);
        scEng                = (Switch)             findViewById(R.id.langEngSwitch);
        scFrench             = (Switch)             findViewById(R.id.langFrSwitch);


        rLayout              = (RelativeLayout)     findViewById(R.id.rLayout);
        storage              = (TextView)           findViewById(R.id.storageTView);
        liAbout              = (LinearLayout)       findViewById(R.id.liAbout);
        cleanPressed         = (IconicsImageButton) findViewById(R.id.clearImgButt);

        AppController.addToDestroyList(this);

        iniListeners();
        loadSetting(true);
    }

    private void iniListeners() {

        c = new CacheImage(SettingsActivity.this,rLayout );
        c.setOnMovingCompleteListener(this);

        pLang = new SettingPresenter(this);
        pLang.setOnSettingChangeListener(this);

        liAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(new Intent(SettingsActivity.this, AboutActivity.class ), SettingsActivity.this, false);
            }
        });

        scEng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!doNothingLang){
                    if (b){
                        pLang.setLanguage(Constants.LANGUAGE_ENGLISH);
                    }
                }
            }
        });

        scFrench.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!doNothingLang){
                    if (b){
                        pLang.setLanguage(Constants.LANGUAGE_FRENCH);
                    }
                }
            }
        });

        scAutoPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    // activity auto auto post
                    Save.defaultSaveBoolean(Constants.PREF_AUTO_POST_SERVICE_IS_ENABLE,true, SettingsActivity.this);
                }else{
                    // disable auto post
                    Save.defaultSaveBoolean(Constants.PREF_AUTO_POST_SERVICE_IS_ENABLE,false, SettingsActivity.this);
                }
            }
        });


        scStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!doNotMove){
                    if (b){
                        if (!isInternalPrefered)
                            c.onMoveToInternal();
                    }
                    else{
                        if (c.externalMemoryAvailable(SettingsActivity.this))
                            c.onMoveToExternal();
                        else
                            c.onSDmissing();
                    }
                }

            }
        });

        cleanPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(
                        SettingsActivity.this);
                dialog.setTitle(getString(R.string.del_cache_title));
                dialog.setMessage(R.string.del_cache_msg);
                dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isAllPermissionGranted()){
                            c.cleanCache();
                            loadSetting(true);
                        }
                    }
                });
                dialog.setNegativeButton(getString(R.string.no), null);
                dialog.show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadSetting(true);
    }

    private void loadSetting(boolean canCheckExt) {
        boolean isAutoEnable = Save.defaultLoadBoolean(Constants.PREF_AUTO_POST_SERVICE_IS_ENABLE, this);

        scAutoPost.setChecked(isAutoEnable);

        doNotMove = true;
        updatePreference();
        doNotMove = false;


        storage.setText(getString(R.string.cache_info) + " "+c.getCacheSize());
        if (c.getCacheSize().equals("0 o")){
            cleanPressed.setVisibility(View.INVISIBLE);
            cleanPressed.setClickable(false);
        }else{
            cleanPressed.setVisibility(View.VISIBLE);
            cleanPressed.setClickable(true);
        }

        if (canCheckExt){
            //User has remove SD carte
            if (!c.shouldUserExternal(this))
                c.onSDRemove(this);
        }

        if (pLang != null){
            onDisplayLang(pLang.getLanguage());
        }



    }

    private void updatePreference() {
        isInternalPrefered = Save.defaultLoadBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL, this);
        scStorage.setChecked(isInternalPrefered);
    }

    @Override
    public void onBackPressed() {
        Utils utils = new Utils();
        utils.onFinishWithAnimation(this);
        super.onBackPressed();
    }

    @Override
    public void onMovingError() {

    }

    @Override
    public void onMovingSucces(boolean fromInternalToExternal) {
        try {
            if (fromInternalToExternal)
                Save.defaultSaveBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL,false, SettingsActivity.this);
            else
                Save.defaultSaveBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL,true, SettingsActivity.this);

            // reset Cache size & prefer storage
            c = null;
            c = new CacheImage(SettingsActivity.this,rLayout );
            c.setOnMovingCompleteListener(this);
            loadSetting(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFolderEmpty() {
        try {
            Save.defaultSaveBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL,false, SettingsActivity.this);
            loadSetting(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onSDRemove() {
        loadSetting(false);
    }

    @Override
    public void onSDNotFound() {
        loadSetting(true);
    }

    @Override
    public void onLanguageChanged(@NonNull String newLang) {
        onDisplayLang(newLang);
        super.onLanguageChanged(newLang);
        recreate();
    }

     private void onDisplayLang(@NonNull String newLang) {
         doNothingLang = true;

         if (newLang.equals(Constants.LANGUAGE_FRENCH)){
             scFrench.setChecked(true);
             Handler h = new Handler();
             h.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 try {
                                     scEng.setChecked(false);
                                     doNothingLang = false;
                                 }catch (Exception e){
                                     e.printStackTrace();
                                 }
                             }
                         });
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                 }
             },200);
         }else {
             scFrench.setChecked(false);
             Handler h = new Handler();
             h.postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 try {
                                     scEng.setChecked(true);
                                     doNothingLang = false;
                                 }catch (Exception e){
                                     e.printStackTrace();
                                 }
                             }
                         });
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                 }
             },200);

         }


     }
 }

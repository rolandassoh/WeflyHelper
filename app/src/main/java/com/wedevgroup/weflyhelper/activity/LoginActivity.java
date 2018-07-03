package com.wedevgroup.weflyhelper.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.task.LoginTask;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.design.AnimeView;

public class LoginActivity extends DBActivity implements LoginTask.OnLoginListener{
    private EditText edName, edPassword;
    private String name = "";
    private String password = "";
    private AnimeView aView;
    private LinearLayout liLogin, liMain, liMainSec, liErrAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iniView();


        iniListener();
        AppController.addToDestroyList(this);

        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin){
            // Stop execution
            if(appController != null)
                appController.cleanToken(this);
            onDisplayUI(true,false,false,true);
            return;
        }


        try {
            String name = Save.defaultLoadString(Constants.PREF_USER_NAME, this);
            String pwd = Save.defaultLoadString(Constants.PREF_USER_PASSWORD, this);

            if (name != null && pwd!= null){
                edName.setText(name);
                edPassword.setText(pwd);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void iniView() {

        liLogin             = (LinearLayout)         findViewById(R.id.liLogin);
        liMain              = (LinearLayout)         findViewById(R.id.liMain);
        liErrAdmin          = (LinearLayout)         findViewById(R.id.liErrAdmin);
        liMainSec           = (LinearLayout)         findViewById(R.id.liMainSec);

        edName              = (EditText)             findViewById(R.id.nameEdText);
        edPassword          = (EditText)             findViewById(R.id.passwordEdText);

    }

    private void iniListener() {
        liLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aView = new AnimeView(liLogin, new AnimeView.OnAnimationEndCallBack() {
                    @Override
                    public void onEnd(@NonNull View view) {
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
                        name            = edName.getText().toString().trim();
                        password        = edPassword.getText().toString().trim();

                        if (isInputValid()){
                            if (appController != null){
                                if (appController.isNetworkAvailable()){
                                    onDisplayUI( true, false, false, false);
                                    LoginTask task = new LoginTask(name, password, LoginActivity.this);
                                    task.setOnLoginListener(LoginActivity.this, liMain);
                                    task.execute();
                                }else
                                    Utils.showToast(LoginActivity.this, R.string.error_no_internet, liMain);

                            }
                        }

                    }
                });
                aView.startAnimation();
            }
        });

    }


    private void onDisplayUI(boolean canDisableBtn, boolean isLoginError, boolean isServerError, boolean isAdmin) {
        if (liMain != null && liLogin != null){
            if (canDisableBtn){
                liLogin.setClickable(false);
            }else
                liLogin.setClickable(true);

            if (isLoginError)
                Utils.showToast(this, R.string.error_login, liMain);

            liMain.setVisibility(View.VISIBLE);

            if (isServerError){
                Utils.showToast(this, R.string.error_api, liMain);
            }

            if (isAdmin){
                liMainSec.setVisibility(View.GONE);
                liErrAdmin.setVisibility(View.VISIBLE);
            }else{
                liMainSec.setVisibility(View.VISIBLE);
                liErrAdmin.setVisibility(View.GONE);
            }


        }

    }

    private boolean isInputValid() {
        if (name.equals("")){
            Utils.showToast(this,R.string.name_empty , liLogin);
            return false;
        }

        if (password.equals("")){
            Utils.showToast(this,R.string.password_empty , liLogin);
            return false;
        }

        return true;

    }


    @Override
    public void onLoginError(@NonNull View view) {
        onDisplayUI( false, true, false, false);
    }

    @Override
    public void onLoginServerError() {
        onDisplayUI(false, false, true, false);
    }



    @Override
    public void onLoginSucces() {
        if (shouldStop())
            return;

        try {
            boolean isOldUser = Save.defaultLoadBoolean(Constants.PREF_IS_SAME_USER, this);
            if (!isOldUser)
                cleanDB();
        }catch (Exception e){
            e.printStackTrace();
        }

        onDisplayUI( false, false, false, false);
        startActivity(new Intent(LoginActivity.this, LoadingActivity.class));
        finish();

    }

    private boolean shouldStop() {
        if (appController != null){
            if (appController.isAdministrator()){
                onDisplayUI(true,false,false,true);
                // Stop execution
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.STATE_USER_NAME,name );
        outState.putString(Constants.STATE_USER_NAME,password );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore old State
        String uName           = savedInstanceState.getString(Constants.STATE_USER_NAME);
        String uPassword       = savedInstanceState.getString(Constants.STATE_USER_PASSWORD);

        if (uName != null && uPassword != null && edPassword != null && edName != null ){
            edName.setText(uName);
            edPassword.setText(uPassword);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Utils utils =  new Utils();
        utils.animActivityClose(this);
    }
}

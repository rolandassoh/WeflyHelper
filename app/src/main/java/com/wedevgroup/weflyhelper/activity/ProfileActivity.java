package com.wedevgroup.weflyhelper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.model.Account;
import com.wedevgroup.weflyhelper.presenter.BaseActivity;
import com.wedevgroup.weflyhelper.task.ProfileGetTask;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.NetworkWatcher;
import com.wedevgroup.weflyhelper.utils.Utils;

import org.json.JSONArray;

public class ProfileActivity extends BaseActivity implements ProfileGetTask.OnProfileDownloadCompleteListener {

    private CoordinatorLayout coLayout;
    private NestedScrollView scView;
    private AppBarLayout appBar;
    private ProgressBar pBar;
    private LinearLayout liServerError, liNetworkErr;
    private int selected;
    private ImageView pict;
    private Account account = null;
    CollapsingToolbarLayout collapsToolbar;
    private String TAG = getClass().getSimpleName();

    private TextView firstName, lastName, userName, email, tel, enterprise, layer, function;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_profile);

        iniViews();

        watcher = new NetworkWatcher(this, coLayout);

        selected = 1;

        watcher.setOnInternetListener(this);

        onDisplayUI(true, false, false);

        checkTokenAndNetwork(this, watcher);

    }

    private void downloadProfile() {
        onDisplayUI(true,false,false);
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ProfileGetTask task = new ProfileGetTask(this);
                task.setOnProfileDownloadCompleteListener(this);
                task.execute();
            }
        }


    }

    private void onDisplayUI(boolean isLoading, boolean isNetworkError , boolean isServerError){
        hideAll();

        if (isLoading){
            pBar.setVisibility(View.VISIBLE);
        }else {
            if (!isNetworkError && !isServerError){
                collapsToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                collapsToolbar.setContentScrim(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
                appBar.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
                scView.setVisibility(View.VISIBLE);
                pict.setVisibility(View.VISIBLE);
            }
        }

        if (isNetworkError)
            liNetworkErr.setVisibility(View.VISIBLE);

        if (isServerError)
            liServerError.setVisibility(View.VISIBLE);

    }

    private void hideAll() {
        collapsToolbar.setBackground(new ColorDrawable(Color.TRANSPARENT));
        collapsToolbar.setContentScrim(new ColorDrawable(Color.TRANSPARENT));
        appBar.setBackground(new ColorDrawable(Color.TRANSPARENT));
        pict.setVisibility(View.INVISIBLE);
        if (scView.getVisibility() != View.INVISIBLE)
            scView.setVisibility(View.INVISIBLE);
        if (pBar.getVisibility() != View.GONE)
            pBar.setVisibility(View.GONE);
        if (liNetworkErr.getVisibility() != View.GONE)
            liNetworkErr.setVisibility(View.GONE);
        if (liServerError.getVisibility() != View.GONE)
            liServerError.setVisibility(View.GONE);

    }

    private void iniViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsToolbar = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }


        coLayout            = (CoordinatorLayout) findViewById(R.id.coLayoutMain);

        appBar              = (AppBarLayout)      findViewById(R.id.app_bar);

        scView              = (NestedScrollView)  findViewById(R.id.nestedSrView);

        pBar                = (ProgressBar)       findViewById(R.id.pBar);

        pict                = (ImageView)         findViewById(R.id.profileImg);

        liServerError       = (LinearLayout)      findViewById(R.id.liServerErrSon);
        liNetworkErr        = (LinearLayout)      findViewById(R.id.liNetworkError);

        firstName           = (TextView)          findViewById(R.id.firstNameTView);
        lastName            = (TextView)          findViewById(R.id.lastNameTView);
        userName            = (TextView)          findViewById(R.id.userNameTView);
        email               = (TextView)          findViewById(R.id.emailTVIew);
        tel                 = (TextView)          findViewById(R.id.phoneTView);
        enterprise          = (TextView)          findViewById(R.id.enterNameTView);
        layer               = (TextView)          findViewById(R.id.layerTView);
        function            = (TextView)          findViewById(R.id.funcTView);


    }

    @Override
    public void onConnected() {
        super.onConnected();
        switch (selected){
            case 1:
                // Display profile
                downloadProfile();
                break;
            default:
                break;
        }
        selected = 0;
    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();
        onDisplayUI(false,true,false);
    }

    @Override
    public void onRetry() {
        super.onRetry();

        if (watcher != null)
            watcher.isNetworkAvailable();
    }

    @Override
    public void onDownloadError(@NonNull String errorMsg) {
        onDisplayUI(false, false, true);
    }

    @Override
    public void onDownloadSucces(@NonNull Account account) {
        this.account = account;
        showProfile(account);
        onDisplayUI(false, false, false);

    }


    @Override
    public void onNetworkError() {
        onDisplayUI(false, true, false);

    }

    private void displayPicture(String imageUrl , final Context ctx, @NonNull final ImageView view ) {

        if(!imageUrl.equals("")){
            try {
                Picasso.with(ctx).load(imageUrl).into(view);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void showProfile(@NonNull Account acc){
        firstName.setText(acc.getFirstName());
        lastName.setText(acc.getLastName());
        userName.setText(acc.getUserName());
        layer.setText(acc.getCouche());
        email.setText(acc.getEmail());
        tel.setText(acc.getProfile().getPhone());
        enterprise.setText(acc.getProfile().getEntreprise());
        function.setText(String.valueOf(acc.getProfile().getFonction()));


        displayPicture(acc.getProfile().getImage(), this, pict);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (account != null)
            outState.putSerializable(Constants.STATE_PROFILE,account );

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore old State
        account = (Account) savedInstanceState.getSerializable(Constants.STATE_PROFILE);

        if (account == null)
            showProfile(account);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Utils utils =  new Utils();
        utils.animActivityClose(this);
    }

    @Override
    protected void onDestroy() {
        removeAsynTasks();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

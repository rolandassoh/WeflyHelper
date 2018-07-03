package com.wedevgroup.weflyhelper.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.baoyz.widget.PullRefreshLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.adapter.ParcelleViewAdapter;
import com.wedevgroup.weflyhelper.model.Culture;
import com.wedevgroup.weflyhelper.model.MessageEB;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.model.Point;
import com.wedevgroup.weflyhelper.model.Profile;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.presenter.LoadingPresenter;
import com.wedevgroup.weflyhelper.presenter.LocationActivity;
import com.wedevgroup.weflyhelper.service.PostParcelleService;
import com.wedevgroup.weflyhelper.task.ParcelleGetAllTask;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.NetworkWatcher;
import com.wedevgroup.weflyhelper.utils.Save;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.AppController;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends LocationActivity implements ParcelleViewAdapter.OnListItemClickListener, OnMenuItemClickListener, ParcelleGetAllTask.OnParcelleLoadingCompleteListener, View.OnClickListener, GuillotineListener {
    private ArrayList<Parcelle> parcelleList = new ArrayList<>()  ;
    ArrayList<Parcelle> listForSyn = new ArrayList<>();
    private LinearLayout liMain, liNoParcel, liServerError;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton fabAdd, fabNav;
    public ParcelleViewAdapter adapter;
    ParcelleGetAllTask task;
    boolean shouldShowDetails = false;
    private SlidingUpPanelLayout mLayout;
    private final String TAG = getClass().getSimpleName();
    private Parcelle parcelSelected = null;
    private Parcelle parUpdated;
    private boolean isUpChecked;
    private Animation apparear, disap;
    AlertDialog dialog;
    int actionBarHeight = 0;
    FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private int selected = 0;
    private boolean guiloIsOpen;
    MaterialRippleLayout btnSend, btnAdd, btnEdit, btnDelete;

    ProgressBar pBar;
    PullRefreshLayout pRefresh;

    private boolean isSearchVisible;
    FrameLayout root;
    Toolbar toolbar;
    View contentHamburger;
    View guillotineMenu;
    private EditText mSearchField;
    private TextView mXMark, title;
    private ImageButton searchBtn;
    private AppCompatImageButton moreBtn;
    private View searchLay;
    private ImageView imgProfile, imgHelp, imgSetting, imgLogout;
    private LinearLayout liProfile, liHelp, liSetting, liLogout;
    GuillotineAnimation.GuillotineBuilder builder;

    private Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // remove notification if exist

        try {
            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(Constants.NOTIFICATION_SERVICE_AUTO_POST_ID);
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);
        }catch (Exception e){
            e.printStackTrace();
        }

        iniViews();

        iniSearchView();

        iniHamburgerMenu();

        iniListeners();

        iniMenuFragment();

        //Clear all instance inBackground
        AppController.clearDestroyList();
//        // Clearn old on new instance
        AppController.addToDestroyList(this);

        // Listen
        EventBus.getDefault().register(MainActivity.this);

        utils = new Utils();

        // show slid panel
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        watcher = new NetworkWatcher(this, liMain);
        watcher.setOnInternetListener(this);

        shouldShowDetails = getIntent().getBooleanExtra("showDetail", false);

        onDisplayParcelles();

        if (shouldShowDetails){
            parUpdated = (Parcelle) getIntent().getSerializableExtra("parcelObj");
            if (parUpdated != null){
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        onClick(parUpdated, MainActivity.this);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, 400);
            }

        }

    }


    private void iniMenuFragment() {
        fragmentManager = getSupportFragmentManager();

        Drawable icSend = getResources().getDrawable( R.drawable.ic_upload_green );
        ColorFilter fiSend = new LightingColorFilter( ContextCompat.getColor(this,R.color.material_green_900)
                , ContextCompat.getColor(this,R.color.material_green_900));
        icSend.setColorFilter(fiSend);

        Drawable icRefresh = getResources().getDrawable(R.drawable.ic_refresh_green);
        ColorFilter filSend = new LightingColorFilter( ContextCompat.getColor(this,R.color.material_green_900)
                , ContextCompat.getColor(this,R.color.material_green_900));
        icRefresh.setColorFilter(filSend);

        Drawable icDownload = getResources().getDrawable( R.drawable.ic_download_green);
        ColorFilter filDownload = new LightingColorFilter( ContextCompat.getColor(this,R.color.material_green_900)
                , ContextCompat.getColor(this,R.color.material_green_900));
        icDownload.setColorFilter(filDownload);

        Drawable icAdd = getResources().getDrawable( R.drawable.ic_add_green);
        ColorFilter filAdd = new LightingColorFilter( ContextCompat.getColor(this,R.color.material_green_900)
                , ContextCompat.getColor(this,R.color.material_green_900));
        icAdd.setColorFilter(filAdd);


        MenuObject send = new MenuObject(getString(R.string.send_all));
        send.setDrawable(icSend);
        send.setBgColor(Color.WHITE);

        MenuObject refresh = new MenuObject(getString(R.string.refresh));
        refresh.setDrawable(icRefresh);
        refresh.setBgColor(Color.WHITE);

        MenuObject download = new MenuObject(getString(R.string.synch));
        download.setDrawable(icDownload);
        download.setBgColor(Color.WHITE);


        MenuObject add = new MenuObject(getString(R.string.add));
        add.setDrawable(icAdd);
        add.setBgColor(Color.WHITE);

        List<MenuObject> menuObjects = new ArrayList<>();
        menuObjects.add(refresh);
        menuObjects.add(send);
        menuObjects.add(download);
        menuObjects.add(add);

        // Calculate ActionBar's height
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize(actionBarHeight);
        menuParams.setMenuObjects(menuObjects);
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);

    }

    private void iniListeners() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add new Parcel
                fabAdd.clearAnimation();
                fabAdd.startAnimation(disap);

                Intent intent = new Intent(MainActivity.this, CreateParcelleActivity.class);

                launchActivity(intent, MainActivity.this, false);

            }
        });

        fabNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parcelSelected != null){
                    Point point = new Point();
                    for (Point dm: parcelSelected.getPointsList()){
                        if (dm.isReference())
                            point = dm;
                    }
                    // Start Navigation
                    // Check permission Before
                    if (!locaPresenter.isLocationEnabled())
                        locaPresenter.showLocationServicesRequireDialog();
                    else if(getCurrentLatitude() == Constants.DOUBLE_NULL && getCurrentLongitude() == Constants.DOUBLE_NULL)
                        Utils.showToast(MainActivity.this, R.string.loading_gps, liMain);
                    else{
                        try {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+ point.getLatitude() +"," + point.getLongitude() + "" );
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                }
            }
        });

        liHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show Help
                if (isAllPermissionGranted()){
                    // check if file existe
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), (getString(R.string.download_title) + Constants.FILE_EXTENSION));
                    if(file.exists()){
                        // file Not Exist
                        String msg = getString(R.string.help_msg_again_left) + " "  + getString(R.string.download_title) +" " + getString(R.string.help_msg_again_right);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.help_title));
                        builder.setMessage(msg);
                        builder.setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Download Video
                                        String url = Constants.VIDEO_TUTO_URL;
                                        String fileExtention = Constants.FILE_EXTENSION;
                                        String fileName = getString(R.string.download_title);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                        request.setDescription(getString(R.string.download_msg));
                                        request.setTitle(fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.allowScanningByMediaScanner();
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtention);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE);
                                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        try {
                                            manager.enqueue(request);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        builder.setNegativeButton(getString(R.string.cancel_dialog),
                                null);
                        builder.show();

                    }else {
                        // file Not Exist
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.help_title));
                        builder.setMessage(getString(R.string.help_msg));
                        builder.setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Download Video
                                        String url = Constants.VIDEO_TUTO_URL;
                                        String fileExtention = Constants.FILE_EXTENSION;
                                        String fileName = getString(R.string.download_title);
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                        request.setDescription(getString(R.string.download_msg));
                                        request.setTitle(fileName);
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.allowScanningByMediaScanner();
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtention);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                                DownloadManager.Request.NETWORK_MOBILE);
                                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        try {
                                            manager.enqueue(request);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        builder.setNegativeButton(getString(R.string.cancel_dialog),
                                null);
                        builder.show();
                    }

                }


            }
        });

        liProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // profile
                launchActivity(new Intent(MainActivity.this, ProfileActivity.class), MainActivity.this, true);
            }
        });

        liLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                if (appController != null)
                    appController.cleanToken(MainActivity.this);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                utils.animActivityOpen(MainActivity.this);
            }
        });

        liSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to setting
                launchActivity(new Intent(MainActivity.this, SettingsActivity.class), MainActivity.this, true);
            }
        });



        //Listen Refresh
        pRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUI();

            }
        });

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                Log.v(Constants.APP_NAME, TAG + "previousState " + previousState.name() + " newState " + newState.name());
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchView();
            }
        });


        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuDialogFragment.show(getSupportFragmentManager(), "ContextMenuDialogFragment");
            }
        });
    }

    private void iniHamburgerMenu() {
        imgProfile.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_user2)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgHelp.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_question_circle2)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgLogout.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_sign_out_alt)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));

        imgSetting.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_cog)
                .color(ContextCompat.getColor(this,R.color.white))
                .sizeDp(Constants.HAMBURGER_ICON_SIZE));



        builder = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger);
        builder.setStartDelay(Constants.RIPPLE_DURATION);
        builder.setActionBarViewForAnimation(toolbar);
        builder.setClosedOnStart(true);
        builder.setGuillotineListener(this);
        builder.build();
    }

    private void iniSearchView() {
        mSearchField = (EditText) findViewById(R.id.search_field);
        mXMark = (TextView) findViewById(R.id.search_x);

        mXMark.setOnClickListener(this);

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();
                ArrayList<Parcelle> searchedArray = new ArrayList<Parcelle>();
                for (Parcelle dm : parcelleList) {
                    if (dm.getRegion().toLowerCase()
                            .contains(searchText.toLowerCase())) {
                        searchedArray.add(dm);
                    }
                }
                if (searchText.isEmpty()) {
                    onDisplayParcelles();
                    mXMark.setText(R.string.fontello_x_mark);
                } else {
                    ParcelleViewAdapter ad = new ParcelleViewAdapter(MainActivity.this, searchedArray, liMain);
                    recyclerView.setAdapter(ad);
                    mXMark.setText(R.string.fontello_x_mark_masked);
                }
            }
        });
    }

    private void refreshUI() {
        if (watcher != null){
            selected = 4;
            watcher.isNetworkAvailable();
        }
    }



    private void iniViews() {
        liMain               = (LinearLayout) findViewById(R.id.liLayout);
        liNoParcel           = (LinearLayout) findViewById(R.id.liNoParcel);
        liServerError        = (LinearLayout) findViewById(R.id.liServerError);



        pBar                 = (ProgressBar) findViewById(R.id.pBar);
        fabAdd               = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabNav               = (FloatingActionButton) findViewById(R.id.fabNav);
        pRefresh             = (PullRefreshLayout) findViewById(R.id.refreshLayout);
        moreBtn              = (AppCompatImageButton) findViewById(R.id.nav_more);

        apparear             = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_grow);
        disap                = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_shrink);


        toolbar              = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception e){
            e.printStackTrace();
        }


        recyclerView          = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager         = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Slide panel ini
        mLayout               = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        // enable anchor
        mLayout.setAnchorPoint(0.7f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        pBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_ATOP);

        fabAdd.setVisibility(View.GONE);


        //Guillotine Menu
        root                            = (FrameLayout) findViewById(R.id.root);
        contentHamburger                = (View)        findViewById(R.id.content_hamburger);

        guillotineMenu                 = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);
        toolbar                         =           (Toolbar)   findViewById(R.id.toolbar);
        searchLay                       =           (View)    findViewById(R.id.searchLayout);
        imgProfile                      =           (ImageView) guillotineMenu.findViewById(R.id.profileImg);
        imgHelp                         =           (ImageView) guillotineMenu.findViewById(R.id.helpImg);
        imgSetting                      =           (ImageView) guillotineMenu.findViewById(R.id.settingImg);
        imgLogout                       =           (ImageView) guillotineMenu.findViewById(R.id.logoutImg);

        liProfile                       =           (LinearLayout) guillotineMenu.findViewById(R.id.liProfile);
        liHelp                          =           (LinearLayout) guillotineMenu.findViewById(R.id.liHelp);
        liSetting                       =           (LinearLayout) guillotineMenu.findViewById(R.id.liSetting);
        liLogout                        =           (LinearLayout) guillotineMenu.findViewById(R.id.liLogout);
        title                           =           (TextView)     findViewById(R.id.titleTextView);



        searchBtn                       =           (ImageButton) findViewById(R.id.searchImgBtn);

        //Fix background Color
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // below lollipop
            ColorStateList csl = ColorStateList.valueOf(Color.TRANSPARENT);
            moreBtn.setSupportBackgroundTintList(csl);
        }



    }

    public void onDisplayUi(boolean canDisplayUi, boolean isEmpty, boolean isNetworkError, boolean isLoading, boolean isSending, boolean canDisplayFab, boolean isApiError ){
        hideAll();
        if (canDisplayUi){
            liMain.setVisibility(View.VISIBLE);
            //play animation
        }

        if (isEmpty){
            if (parcelleList.size() > 0){
                liMain.setVisibility(View.VISIBLE);
            }else
                liNoParcel.setVisibility(View.VISIBLE);
            //play Animation
        }

        if (isNetworkError){
            try {
                liMain.setVisibility(View.VISIBLE);

                Snackbar snackbar = Snackbar
                        .make(liMain, getResources().getString(R.string.error_no_internet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onDisplayParcelles();
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                snackbar.show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        if (isLoading){
            pBar.setVisibility(View.VISIBLE);
            //play Animation
        }
        if (canDisplayFab)
            showBtnAdd();

        if (adapter!= null)
            adapter.notifyDataSetChanged();

        if (isSending){
            try {
                dialog = new SpotsDialog(this, R.style.SpotAlertDialog);
                dialog.show();
                TextView title = (TextView) dialog.findViewById(R.id.dmax_spots_title);
                title.setText(getString(R.string.loading_uploading));
            } catch (Exception e){
                e.printStackTrace();
            }
        }else {
            if (dialog != null)
                dialog.dismiss();
        }

        if (isApiError){
            liServerError.setVisibility(View.VISIBLE);
            liMain.setVisibility(View.GONE);
        }
    }

    private void onDisplayParcelles() {
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                task = new ParcelleGetAllTask(this, false, liMain);
                task.setOnParcelleLoadingCompleteListener(this);
                task.run();
            }
        }

    }

    private void hideAll(){
        if (liMain.getVisibility() != View.GONE)
            liMain.setVisibility(View.GONE);
        if (liNoParcel.getVisibility() != View.GONE)
            liNoParcel.setVisibility(View.GONE);
        if (liServerError.getVisibility() != View.GONE)
            liServerError.setVisibility(View.GONE);
        if (pBar.getVisibility() != View.GONE)
            pBar.setVisibility(View.GONE);
        pRefresh.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }else if (isSearchVisible)
            hideSearchView();
        else if (guiloIsOpen){
            //Close Guillotine MENU
            if (builder != null){
                findViewById(R.id.guillotine_hamburger).performClick();
            }else{
                closeApp();
            }
        }else
            closeApp();
    }

    private void closeApp() {
        if (utils != null){
            MainActivity.this.finish();
            AppController.clearDestroyList();
            AppController.clearAsynTask();
            utils.animActivityClose(this);
        }

    }


    @Override
    protected void onRestart() {
        onDisplayParcelles();
        super.onRestart();
    }

    private void showBtnAdd() {
        fabAdd.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                fabAdd.clearAnimation();
                                fabAdd.setVisibility(View.VISIBLE);
                                fabAdd.startAnimation(apparear);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 800);
    }

    @Override
    public void onClick(@NonNull final Parcelle p, @NonNull DBActivity act) {
        TextView gName, gTel, gEmail , date, couche, zone, perimetre, region,  superficie, cultures;
        ImageView pImage;
        ArrayList<Culture> culturesList = new ArrayList<Culture>();

        try{
            gName               = (TextView) mLayout.findViewById(R.id.gNameTView);
            gTel                = (TextView) mLayout.findViewById(R.id.gTelView);
            gEmail              = (TextView) mLayout.findViewById(R.id.gEmailTView);
            date                = (TextView) mLayout.findViewById(R.id.dateTView);
            couche              = (TextView) mLayout.findViewById(R.id.coucheTView);
            zone                = (TextView) mLayout.findViewById(R.id.zoneTView);
            perimetre           = (TextView) mLayout.findViewById(R.id.perimetreTView);
            superficie          = (TextView) mLayout.findViewById(R.id.surfaceTView);
            region              = (TextView) mLayout.findViewById(R.id.regionTView) ;
            cultures            = (TextView) mLayout.findViewById(R.id.cultureTView);

            pImage              = (ImageView) mLayout.findViewById(R.id.image);

            btnSend             = (MaterialRippleLayout) mLayout.findViewById(R.id.ripSend);
            btnAdd              = (MaterialRippleLayout) mLayout.findViewById(R.id.ripAdd);
            btnEdit             = (MaterialRippleLayout) mLayout.findViewById(R.id.ripEdit);
            btnDelete           = (MaterialRippleLayout) mLayout.findViewById(R.id.ripDelete);

            // ini listener
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected = 1;
                    watcher.isNetworkAvailable();

                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDetailPanel();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                launchActivity(new Intent(getApplicationContext(), CreateParcelleActivity.class), MainActivity.this,false);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, 200);


                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDetailPanel();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (parcelSelected != null){
                                    Intent edIntent = new Intent(getApplicationContext(), CreateParcelleActivity.class);
                                    edIntent.putExtra("toEdit", true);
                                    Bundle b = new Bundle();
                                    b.putSerializable("parcelObj", parcelSelected);
                                    edIntent.putExtras(b);
                                    launchActivity(edIntent, MainActivity.this, false);

                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, 200);

                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDetailPanel();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            removeParcelle(parcelSelected, MainActivity.this ,mLayout );
                        }
                    }, 200);


                }
            });

            //update UI
            gName.setText(p.getNameGuide());
            gTel.setText(p.getTelGuide());
            gEmail.setText(p.getEmailGuide());
            date.setText(p.getDateCreated());
            couche.setText(p.getCouche());
            zone.setText(p.getZone());
            region.setText(p.getRegion());
            String per = Utils.formatDouble( p.getPerimetre())  + " " +  getString(R.string.meters);
            String area = Utils.formatDouble(p.getSurface()) +  " " + getString(R.string.hectares);
            perimetre.setText(per);
            superficie.setText(area);



            try {
                pImage.setImageBitmap(p.getImageAsBitmap(act));
            }catch (Exception e){
                e.printStackTrace();
            }

            String culList = "";
            culturesList.addAll(p.getCultureList());
            if (culturesList.size() > 1){
                for (int i =0; i < culturesList.size() -1 ; i++){
                    culList += culturesList.get(i).getName() +", ";
                }

                culList += culturesList.get(culturesList.size()  - 1).getName() ;
            }else if (culturesList.size() > 0){
                culList += culturesList.get(culturesList.size()  - 1).getName() ;
            }
            cultures.setText(culList);


            // share
            parcelSelected = p;


            if (mLayout != null){
                if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                else
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void hideDetailPanel(){
        if (mLayout != null){
            if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED)
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }


    public SlidingUpPanelLayout getUI(){
        return mLayout;
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Subscribe
    public void onEvent(MessageEB mMsg){
        final MessageEB m = mMsg;
        if(mMsg.getClassSender().equalsIgnoreCase(MainActivity.class+""))
            return;
        if (mMsg.getMsg().trim().equals(Constants.RESPONSE_CODE_ERROR_UPLOAD)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MainActivity.super.onSendError(new Parcelle(), liMain, true);
                        onDisplayUi(true,false,false, false, false, false, false);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else if (mMsg.getMsg().trim().equals(Constants.RESPONSE_CODE_UPLOAD_OK)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        onDisplayParcelles();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else if (mMsg.getMsg().trim().equals(Constants.RESPONSE_CODE_ERROR_NO_INTERNET)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Utils.showToast(MainActivity.this, R.string.error_ping ,liMain);
                        onDisplayUi(true,false,false, false, false, false, false);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }else {
            // permission  error
        }


    }

    @Override
    protected void onDestroy() {
        removeAsynTasks();
        super.onDestroy();
        EventBus.getDefault().unregister(MainActivity.this);
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {

        switch (position){
            case 0:
                //Refresh list
                onDisplayParcelles();
                break;
            case 1:
                // upload all
                //get user position
                boolean needUpload = false;

                if (parcelleList.size() > 0){
                    for (Parcelle dm: parcelleList){
                        if (dm.getDateSoumission().toString().trim().equals("")){
                            needUpload = true;
                            break;
                        }

                    }
                }


                if (needUpload){
                    selected = 2;
                    watcher.isNetworkAvailable();
                }else{
                    Utils.showToast(MainActivity.this,R.string.empty_upload_list ,liMain);
                }


                break;

            case 2:
                if (isAllPermissionGranted){
                    selected = 3;
                    watcher.isNetworkAvailable();
                }
                break;
            default:
                break;
        }

//

    }

    @Override
    public void onDeleteSucces(@NonNull Parcelle p, @NonNull View view) {
        super.onDeleteSucces(p, view);
        onDisplayParcelles();
        //onDisplayUi(parcelleList.size() > 0, parcelleList.size() == 0, false, false);
    }

    @Override
    public void onEditSucces(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast) {
        super.onEditSucces(p, view, canDisplayToast);
        onDisplayParcelles();
    }


    @Override
    public void onSaveSucces(@NonNull Parcelle p, @NonNull View view) {
        super.onSaveSucces(p, view);
        onDisplayParcelles();
    }

    @Override
    public void onSendError(@NonNull Parcelle p, @NonNull View view, boolean many) {
        super.onSendError(p, view, many);
        onDisplayUi(true,false,false, false, false, false, false);
    }

    @Override
    public void onSendSucces(@NonNull Parcelle p, @NonNull View view) {
        super.onSendSucces(p, view);
        onDisplayParcelles();
    }

    @Override
    public void onConnected() {
        super.onConnected();

        switch (selected){
            case 1:
                // POST PARCELLE ITEM
                hideDetailPanel();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mLayout!= null){
                                if (parcelSelected != null){
                                    if (parcelSelected.getDateSoumission().trim().equals("")){
                                        onDisplayUi(true,false,false, false, true, false, false);
                                        // stop service
                                        MessageEB m = new MessageEB();
                                        m.setClassSender(MainActivity.class+"");
                                        m.setMsg(Constants.RESPONSE_STOP_SERVICE);

                                        EventBus.getDefault().post(m);
                                        // send
                                        sendParcelle(parcelSelected, mLayout, MainActivity.this);
                                    }
                                    else
                                        Utils.showToast(MainActivity.this, R.string.parcel_uploaded_alredy, liMain);
                                }

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, 200);
                break;
            case 2:
                // POST ALL PARCELS
                Save.defaultSaveBoolean(Constants.THREAD_AUTO_POST_IS_RUNNING, false, this);
                Intent intent = new Intent(this, PostParcelleService.class);
                intent.putExtra("isOneSend", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startService(intent);
                onDisplayUi(true,false,false, false, true, true, false);
                break;

            case 3:
                // DOWNLOAD
                boolean shouldSync = true;
                Utils.parcellesToLog(TAG, listForSyn, "listForSyn");
                listForSyn.addAll(getParcelles());
                for (Parcelle dm: listForSyn){
                    if (dm.getDateSoumission().toString().trim().equals("")){
                        shouldSync = false;
                        break;
                    }

                }

                if (shouldSync){
                    if (appController != null){
                        if (appController.isTokenAndUserOk(getApplicationContext())){
                            ParcelleGetAllTask task = new ParcelleGetAllTask(this, true, liMain);
                            task.setOnParcelleLoadingCompleteListener(this);
                            task.run();
                        }
                    }


                }else {
                    Utils.showToast(MainActivity.this,R.string.sync_error ,liMain);
                }
                break;
            case 4:
                //REFRESH
                onDisplayParcelles();
                break;
            case 5:
                // check update
                LoadingPresenter presenter = new LoadingPresenter(this);
                presenter.checkUpdate();
                isUpChecked = true;
                break;
            default:
                break;
        }

        selected = 0;
    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();
        pRefresh.setRefreshing(false);
    }

    @Override
    public void onRetry() {
        super.onRetry();
        refreshUI();
    }

    @Override
    public void onLoadError() {
        onDisplayUi(false,false,false, false, false, true, true);

    }

    @Override
    public void onSucces(@NonNull ArrayList<Parcelle> parcelles) {
        parcelleList.clear();
        for (Parcelle pm: parcelles){
            // not delete
            if (!pm.isDelete())
                parcelleList.add(pm);
        }

        if (parcelleList.size() > 0){
            adapter = new ParcelleViewAdapter(MainActivity.this,parcelleList, liMain);
            adapter.setOnListItemClickListener(MainActivity.this, MainActivity.this);
            recyclerView.setAdapter(adapter);
            onDisplayUi(true, false, false, false, false, false, false);
        }else{
            onDisplayUi(false, true, false, false, false, false, false);
        }

        // Check update
        if (!isUpChecked){
            selected = 5;
            watcher.isNetworkAvailableWithSilent();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_x:
                mSearchField.setText(null);
                hideSearchView();
                break;

        }
    }

    private void hideSearchView() {
        searchBtn.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        mSearchField.setText(null);

        //Hide keyboard
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchField.getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }

        searchLay.setVisibility(View.GONE);
        isSearchVisible = false;
    }

    private void showSearchView() {
        searchBtn.setVisibility(View.GONE);
        title.setVisibility(View.GONE);

        mSearchField.requestFocus();
        //show keyboard
        try{
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }catch (Exception e){
            e.printStackTrace();
        }

        searchLay.setVisibility(View.VISIBLE);
        isSearchVisible = true;

    }

    @Override
    public void onGuillotineOpened() {
        guiloIsOpen = true;
    }

    @Override
    public void onGuillotineClosed() {
        guiloIsOpen = false;
    }
}

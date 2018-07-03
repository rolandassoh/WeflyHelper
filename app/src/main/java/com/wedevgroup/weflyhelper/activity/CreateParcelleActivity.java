package com.wedevgroup.weflyhelper.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapCustom;
import com.google.android.gms.maps.MapViewCustom;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallBackCustom;
import com.google.android.gms.maps.PointCustom;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.model.Point;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.presenter.DrawingPresenter;
import com.wedevgroup.weflyhelper.presenter.LocationPresenter;
import com.wedevgroup.weflyhelper.service.LocationProviderService;
import com.wedevgroup.weflyhelper.utils.CacheImage;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.AppController;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import dmax.dialog.SpotsDialog;

public class CreateParcelleActivity extends DBActivity implements  GoogleMapCustom.OnMarkerDragListener, DrawingPresenter.OnDrawingComleteListener{

    private ImageButton btnClean, btnUndo, btnAdd, btnRef, btnDone;
    private CoordinatorLayout coLayout;
    private CoordinatorLayout clCalculation;
    private String TAG = getClass().getSimpleName();
    private GoogleMapCustom map = null;
    public MapViewCustom mapViewCustom;
    private Animation slideUp, fadeIn, fadeOut;
    private LocationPresenter locPresenter;
    boolean isAddPressed = false;
    private TextView tvArea, tvPerimeter;
    private DrawingPresenter draPresenter;
    private boolean isEditMode;
    private double area;
    private double perimeter;
    Parcelle parcelToUpd = null;
    private Parcelle parcel = new Parcelle();
    private Toolbar toolbar;
    private boolean mustShowPos;
    CoordinatorLayout coordinatorLayout;

    LinearLayout liMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parcelle);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        iniViews();

        iniListeners();
        locPresenter = new LocationPresenter(this);
        AppController.addToDestroyList(this);

        mapViewCustom = (MapViewCustom) findViewById(R.id.mapView);
        try {
            mapViewCustom.onCreate(savedInstanceState);
            mapViewCustom.onResume(); // needed to get the map to display immediately
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isEditMode = getIntent().getBooleanExtra("toEdit", false);
        if (isEditMode){
            parcelToUpd = (Parcelle) getIntent().getSerializableExtra("parcelObj");
            mustShowPos = false;
        }else
            mustShowPos = true;

        mapViewCustom.getMapAsync(new OnMapReadyCallBackCustom() {
            @Override
            public void onMapReady(GoogleMapCustom googleMapCustom) {
                map = googleMapCustom;
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(true);
                map.setOnMarkerDragListener(CreateParcelleActivity.this);

                draPresenter = new DrawingPresenter(CreateParcelleActivity.this, map, coLayout);
                draPresenter.setOnDrawingComleteListener(CreateParcelleActivity.this);

                if (!mustShowPos){
                    showPanel();
                }else
                    hidePanel();


                if (isEditMode){
                    if (parcelToUpd != null)
                        draPresenter.onEditMode(parcelToUpd);
                }

                if(draPresenter.isStateAvaiable())
                    draPresenter.onReloadRestoreState();

            }
        });

        onShowLocation();
    }

    private void showPanel() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        if (coordinatorLayout != null){
            coordinatorLayout.setVisibility(View.VISIBLE);
            coordinatorLayout.startAnimation(slideUp);
            mustShowPos = false;
        }
    }
    private void hidePanel(){
        if (coordinatorLayout != null){
            coordinatorLayout.setVisibility(View.GONE);
            mustShowPos = true;
        }

    }

    private void iniViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        btnClean = (ImageButton) findViewById(R.id.cleanImgBtn);
        btnUndo             = (ImageButton) findViewById(R.id.undoImgBtn);
        btnAdd              = (ImageButton) findViewById(R.id.addImgBtn);
        btnDone             = (ImageButton) findViewById(R.id.doneImgBtn);
        btnRef              = (ImageButton) findViewById(R.id.refImgBtn);

        tvArea              = (TextView) findViewById(R.id.areaTView);
        tvPerimeter         = (TextView) findViewById(R.id.perimeterTView) ;


        coLayout            = (CoordinatorLayout) findViewById(R.id.coLayoutMain);
        clCalculation       = (CoordinatorLayout) findViewById(R.id.calculationCoodLayout);
        slideUp             = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        fadeIn              = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeOut             = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        liMap               = (LinearLayout) findViewById(R.id.liMapSnapshot);

    }

    private void iniListeners() {
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draPresenter.requestClearPermission();
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map != null){

                    if (draPresenter.getDrawingSize()> 0){
                        draPresenter.onUndo();
                    }else
                        btnClean.performClick();

                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllPermissionGranted()){
                    if (map!= null){
                        onCaptureLocation(getCurrentLatitude(), getCurrentLongitude());
                    }
                    else
                        Utils.showToast(getApplicationContext(),R.string.loading_map, coLayout);
                }else {
                    isAddPressed = true;
                    onShowLocation();
                }

            }
        });

        btnRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draPresenter.addDrawingOnMap(new LatLng(getCurrentLatitude(), getCurrentLongitude()), true);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (draPresenter.isParcelle()){
                    // save Image
                    final AlertDialog dialog;

                    try {
                        dialog = new SpotsDialog(CreateParcelleActivity.this, R.style.SpotAlertDialog);
                        dialog.show();

                        TextView title = (TextView) dialog.findViewById(R.id.dmax_spots_title);
                        title.setText(CreateParcelleActivity.this.getString(R.string.loading));

                        draPresenter.centerCameraOnParcel();

                        map.snapshot(new GoogleMapCustom.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {

                                CacheImage c = new CacheImage(getApplicationContext());
                                boolean canSave = c.isInternalSpaceEnough(bitmap);
                                ArrayList<Point> points = new ArrayList<Point>();
                                if (canSave){
                                    // compress and Save
                                    try {
                                        Bitmap bmp = Utils.scaleBitmapToMaxSize(800, bitmap);

                                        // Compress
                                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 85, out);
                                        byte[] byteArray  = out.toByteArray();

                                        // convert to bitmap
                                        Bitmap finalImg = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.length);


                                        c.saveTemp(finalImg);

                                        ArrayList<PointCustom> arrtoConvert = new ArrayList<PointCustom>();// drawing without center & reference
                                        arrtoConvert.addAll(draPresenter.getPointsParcels());
                                        for (PointCustom pm: arrtoConvert){
                                            points.add(pm.getPoint());
                                        }
                                        points.add(draPresenter.getCenter().getPoint());
                                        points.add(draPresenter.getReference().getPoint());

                                        // remove loading
                                        dialog.dismiss();

                                        // continue
                                        Intent formIntent = new Intent(CreateParcelleActivity.this, CreateParcelleFormActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("pointsArray", points);
                                        if (isEditMode){
                                            formIntent.putExtra("toEdit", true);
                                            if (parcelToUpd != null){
                                                parcelToUpd.setSurface(area);
                                                parcelToUpd.setPerimetre(perimeter);
                                                bundle.putSerializable("parcelObj", parcelToUpd);
                                            }

                                        }else{
                                            parcel.setSurface(area);
                                            parcel.setPerimetre(perimeter);
                                            bundle.putSerializable("parcelObj", parcel);
                                        }
                                        formIntent.putExtras(bundle);

                                        if (appController != null)
                                        {
                                            if (appController.isTokenAndUserOk(CreateParcelleActivity.this)){
                                                startActivity(formIntent);
                                                bitmap.recycle();
                                                bitmap = null;
                                            }else
                                                return;
                                        }

                                    } catch (OutOfMemoryError exception) {
                                        exception.printStackTrace();
                                    }
                                }else
                                    Utils.showToast(CreateParcelleActivity.this, R.string.storage_low, coLayout );
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }



    private void clearAnim(){
        btnDone.clearAnimation();
        btnRef.clearAnimation();
        btnAdd.clearAnimation();
    }


    @Override
    public void onMarkerDragStart(final PointCustom p) {
        draPresenter.onMarkerDragStart(p);
    }

    @Override
    public void onMarkerDrag(final PointCustom p) {
    }

    @Override
    public void onMarkerDragEnd(final PointCustom p) {
        draPresenter.onMarkerDragEnd(p);
    }


    private void onCaptureLocation(@NonNull Double pLat, @NonNull Double pLong) {
        if(pLat == Constants.DOUBLE_NULL && pLong == Constants.DOUBLE_NULL)
            Utils.showToast(this, R.string.loading_gps, coLayout);
        else {
            Utils.showToast(this, R.string.catch_start, coLayout);
            draPresenter.addDrawingOnMap(new LatLng(pLat, pLong), false);
        }

    }



    public void displayCalculation(@NonNull final CopyOnWriteArrayList<PointCustom> list){
        ArrayList<LatLng> mList = new ArrayList<>();
        for (PointCustom dm: list){
            mList.add(dm.getPoint().getLatLng());
        }


        if (list.size() > 2){
            area = SphericalUtil.computeArea(mList)* 0.0001;
            perimeter = SphericalUtil.computeLength(mList);
            String strArea = Utils.formatDouble( area);
            String strPerimeter = Utils.formatDouble(perimeter);

            tvArea.setText(strArea + " " + getString(R.string.abrev_hectare));
            tvPerimeter.setText(strPerimeter + " " + getString(R.string.abrev_meter));

            if (tvArea.getText().toString().startsWith(","))
                tvArea.setText("0" + tvArea.getText().toString());

            if (tvPerimeter.getText().toString().startsWith(","))
                tvPerimeter.setText("0" + tvPerimeter.getText().toString());

        }

    }

    public void onDisplayPanel(boolean canDisplayDone, boolean canAddRef, boolean canDisplayAdd){
        clearAnim();
        if (canDisplayDone){
            if (btnDone.getVisibility() != View.VISIBLE){
                btnDone.setVisibility(View.VISIBLE);
                btnDone.startAnimation(fadeIn);
            }
        }else {
            btnDone.startAnimation(fadeOut);
            btnDone.setVisibility(View.GONE);
        }

        if (canAddRef){
            if(btnRef.getVisibility() != View.VISIBLE){
                btnRef.setVisibility(View.VISIBLE);
                btnRef.startAnimation(fadeIn);
            }
        } else{
            btnRef.startAnimation(fadeOut);
            btnRef.setVisibility(View.GONE);
        }

        if (canDisplayAdd){
            if(btnAdd.getVisibility() != View.VISIBLE){
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.startAnimation(fadeIn);
            }
        } else{
            btnAdd.startAnimation(fadeOut);
            btnAdd.setVisibility(View.GONE);
        }



        clearAnim();
    }




    private void onShowLocation() {
        // Check permission Before
        if (locPresenter != null){
            if (!locPresenter.isLocationEnabled())
                locPresenter.showLocationServicesRequireDialog();
            else {
                if(locPresenter.canRequestLocation()){
                    //get user position
                    startLocationService();

                }
            }
        }

    }



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Already done by super
//            latitude  = Double.valueOf(intent.getStringExtra("latitude"));
//            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            if (mustShowPos){
                if (appController != null){
                    appController.cameraOnUser(getCurrentLatitude(), getCurrentLongitude(), map);
                    mustShowPos = false;
                    showPanel();
                }
            }

            if (isAddPressed){
                onCaptureLocation(getCurrentLatitude(),getCurrentLongitude());
                isAddPressed = false;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationProviderService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy(){
        //unregisterReceiver(broadcastReceiver);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Utils utils =  new Utils();
        utils.animActivityClose(this);
    }

    public void showCalculationBar(){
        if (clCalculation.getVisibility() != View.VISIBLE){
            clCalculation.setVisibility(View.VISIBLE);
            clCalculation.startAnimation(slideUp);
        }
    }

    public void hideCalcultionBar(){
        clCalculation.clearAnimation();
        clCalculation.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(draPresenter.onSaveInstanceState(outState));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        DrawingPresenter presenter = new DrawingPresenter(this, map, coLayout);
        presenter.setOnDrawingComleteListener(this);
        presenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onClearSucces() {
        onDisplayPanel(false, false, true);
    }

    @Override
    public void onUndo() {

    }

    @Override
    protected void onRestart() {
        onShowLocation();
        super.onRestart();
    }
}

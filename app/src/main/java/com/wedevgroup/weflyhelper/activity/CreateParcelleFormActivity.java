package com.wedevgroup.weflyhelper.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pchmn.materialchips.ChipsInput;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.model.Culture;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.model.Point;
import com.wedevgroup.weflyhelper.model.Region;
import com.wedevgroup.weflyhelper.presenter.DBActivity;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.NetworkWatcher;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.validator.EmailValidator;
import com.wedevgroup.weflyhelper.utils.validator.InputValidator;
import com.wedevgroup.weflyhelper.utils.validator.PhoneValidator;
import com.wedevgroup.weflyhelper.utils.AppController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateParcelleFormActivity extends DBActivity implements DatePickerDialog.OnDateSetListener {
    private TextView btnDate, btnSave, btnCancel, btnReset;
    private TextView tvZone, tvLayer ;
    private EditText edgName, edgEmail, edgPhone;
    ChipsInput  ciCultures, ciRegion;
    private ArrayList<Culture> culturesList;
    private ArrayList<Culture> culturesSelected = new ArrayList<Culture>() ;
    private ArrayList<Region> regionsList;
    private ArrayList<Region> regionSelected = new ArrayList<Region>() ;
    private ArrayList<Point> points;
    private ArrayList<EditText> fielList = new ArrayList<EditText>();
    private boolean isEditMode;
    public DatePickerDialog dpd;
    private final String TAG = getClass().getSimpleName();
    private Toolbar toolbar;
    private Parcelle parcel;
    public LinearLayout liLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parcelle_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        iniViews();

        iniListeners();

        parcel = (Parcelle) getIntent().getSerializableExtra("parcelObj");
        if (parcel == null){
            parcel = new Parcelle();
        }
        if (appController != null){
            parcel.setEntrepriseId(appController.getUserEnterId());
            parcel.setCouche(appController.getUserCouche());
            parcel.setCoucheId(appController.getUserCoucheId());
            parcel.setZone(getString(R.string.zone_msg));
            tvLayer.setText(appController.getUserCouche());
            tvZone.setText(getString(R.string.zone_msg));
        }


        points = (ArrayList<Point>) getIntent().getSerializableExtra("pointsArray");
        if (points != null)
            parcel.setPointsList(points);

        isEditMode = getIntent().getBooleanExtra("toEdit", false);
        watcher = new NetworkWatcher(this, liLayout);
        watcher.setOnInternetListener(this);
        AppController.addToDestroyList(this);

        onDisplayUi(parcel, true, true);



    }

    private void iniListeners() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        CreateParcelleFormActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()){
                    saveInput();

                    if (isEditMode)
                        updateParcelle(parcel, true, liLayout, true);
                    else
                        addParcelle(parcel,true, liLayout);


                }

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
            }
        });

    }

    private void saveInput() {
        culturesSelected.clear();
        regionSelected.clear();
        List<Culture> list = (List<Culture>) ciCultures.getSelectedChipList();
        List<Region> rList = (List<Region>) ciRegion.getSelectedChipList();


        for (Culture mDm: list){
            culturesSelected.add(mDm);
        }

        for (Region rDm: rList){
            regionSelected.add(rDm);
        }
        if (parcel != null){
            if (culturesSelected.size() > 0)
                parcel.setCultureList(culturesSelected);
            if (regionSelected.size() > 0 ){
                parcel.setRegion(regionSelected.get(0).getName());
                parcel.setRegionId(regionSelected.get(0).getRegionId());

            }

            parcel.setNameGuide(edgName.getText().toString().trim());
            parcel.setEmailGuide(edgEmail.getText().toString().trim());
            parcel.setTelGuide(edgPhone.getText().toString().trim());
            parcel.setZone(tvZone.getText().toString().trim());
            parcel.setCouche(tvLayer.getText().toString().trim());
        }
    }

    private boolean isInputValid() {
        String gName = edgName.getText().toString().trim();
        String gEmail = edgEmail.getText().toString().trim();
        String gTel = edgPhone.getText().toString().trim();

        List<Culture> list = (List<Culture>) ciCultures.getSelectedChipList();

        for (Culture mDm: list){
            culturesSelected.add(mDm);
        }

        if( !(culturesSelected.size() > 0)){
            Utils.showToast(this,R.string.empty_cultures, liLayout);
            return false;
        }

        List<Region> rList = (List<Region>) ciRegion.getSelectedChipList();

        for (Region rDm: rList){
            regionSelected.add(rDm);
        }

        if (!(regionSelected.size() > 0)){
            Utils.showToast(this,R.string.empty_region, liLayout);
            return false;
        }


        String name = InputValidator.isNameValid(this, gName);
        if( !name.equals("")){
            edgName.setError(name);
            return false;
        } else {
            edgName.setError(null);
        }

        String mail = EmailValidator.isEmailValid(this, gEmail);
        if (!mail.equals("")){
            edgEmail.setError(mail);
            return false;
        } else {
            edgEmail.setError(null);
        }

        String phone = PhoneValidator.isPhoneValid(this, "+225" + gTel);
        if( !phone.equals("")){
            edgPhone.setError(phone);
            return false;
        } else {
            edgPhone.setError(null);
        }






        return true;
    }

    private void resetFields() {
        for (EditText dm : fielList){
            dm.setText("");
        }

        iniChipInput(true);
    }

    private void iniViews() {
        btnReset    =                  (TextView) findViewById(R.id.btnReset);
        btnSave     =                  (TextView) findViewById(R.id.btnSave);
        btnCancel   =                  (TextView) findViewById(R.id.btnCancel);
        btnDate     =                  (TextView) findViewById(R.id.dateBtn);

        tvZone      =                  (TextView) findViewById(R.id.zoneTView);
        tvLayer     =                  (TextView) findViewById(R.id.coucheTView);

        ciCultures  =                  (ChipsInput) findViewById(R.id.culturesCi);
        ciRegion    =                  (ChipsInput) findViewById(R.id.regionCi);

        edgName     =                  (EditText) findViewById(R.id.guideNameEt);
        edgEmail    =                  (EditText) findViewById(R.id.guideEmailEt);
        edgPhone    =                  (EditText) findViewById(R.id.guideTelEt);

        liLayout    =                  (LinearLayout) findViewById(R.id.coLayoutMain);


        fielList.add(edgName);
        fielList.add(edgEmail);
        fielList.add(edgPhone);

        //Set focus
        ciCultures.requestFocus();


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        String dateSelected = Utils.convertDate(calendar.getTimeInMillis());
        parcel.setDateCreated(dateSelected);
        try {
            btnDate.setText(dateSelected.substring(0, 10));
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        removeAsynTasks();
        Utils utils = new Utils();
        utils.onFinishWithAnimation(this);
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onSaveError(@NonNull Parcelle p, @NonNull View view) {
        super.onSaveError(p, view);
    }

    @Override
    public void onSaveSucces(@NonNull Parcelle p, @NonNull View view) {
        super.onSaveSucces(p, view);

        onDiplayParcelleDetails(CreateParcelleFormActivity.this, p);
    }


    @Override
    public void onEditError(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast) {
        super.onEditError(p, view, canDisplayToast);
    }

    @Override
    public void onEditSucces(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast) {
        super.onEditSucces(p, view, canDisplayToast);
        onDiplayParcelleDetails(CreateParcelleFormActivity.this, p);


    }

    private  void onDisplayUi( @NonNull Parcelle par, boolean canCheckZone, boolean clearnField){

        iniViews();
        iniChipInput(false);

        if (clearnField){
            edgName.setText(par.getNameGuide());
            edgEmail.setText(par.getEmailGuide());
            edgPhone.setText(par.getTelGuide());
            tvZone.setText(par.getZone());
            tvLayer.setText(par.getCouche());

            //Existing Parcelle
            // old Selected Culture
            if (par.getCultureList().size() > 0){
                for (Culture cu: par.getCultureList()){
                    ciCultures.addChip(cu);
                }
            }
            if (!par.getRegion().trim().equals("")){
                //old Selected  region
                for (Region rg: regionsList){
                    if (rg.getName().toString().trim().equals(par.getRegion())){
                        ciRegion.addChip(rg);
                        break;
                    }
                }
            }

            // New Record
            if (par.getDateCreated().trim().equals("")){
                //set Date --> Today
                Calendar c = Calendar.getInstance();
                onDateSet(null, c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
            }else {
                // Edit Mode
                Calendar cal = Utils.getDateFromString(par.getDateCreated());
                if (cal != null)
                    onDateSet(null,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
            }
        }
    }

    private void iniChipInput(boolean canClear) {
        //------------------------- fill Chip input -----------
        // get Cultures

        if (culturesList == null)
            culturesList = getCultures(this);
        // getRegion
        if (regionsList  == null)
            regionsList = getRegions(this);

        if (culturesList.size() > 0){
            if (canClear){
                for (Culture cu: culturesList){
                    ciCultures.removeChipByLabel(cu.getLabel());
                }
            }
            ciCultures.setFilterableList(culturesList);
        }


        if (regionsList != null && regionsList.size() > 0){
            if (canClear){
                for (Region ru: regionsList){
                    ciRegion.removeChipByLabel(ru.getLabel());
                }
            }
            ciRegion.setFilterableList(regionsList);
            ciRegion.setMaxIsOne(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Prepare Save
        culturesSelected.clear();
        regionSelected.clear();
        Parcelle parceltoSave = new Parcelle();
        List<Culture> list = (List<Culture>) ciCultures.getSelectedChipList();
        List<Region> rList = (List<Region>) ciRegion.getSelectedChipList();


        for (Culture mDm: list){
            culturesSelected.add(mDm);
        }

        for (Region rDm: rList){
            regionSelected.add(rDm);
        }

        if (culturesSelected.size() > 0)
            parceltoSave.setCultureList(culturesSelected);
        if (regionSelected.size() > 0 )
            parceltoSave.setRegion(regionSelected.get(0).getName());
        parceltoSave.setNameGuide(edgName.getText().toString().trim());
        parceltoSave.setEmailGuide(edgEmail.getText().toString().trim());
        parceltoSave.setTelGuide(edgPhone.getText().toString().trim());
        parceltoSave.setZone(tvZone.getText().toString().trim());
        parceltoSave.setCouche(tvLayer.getText().toString().trim());

        if (isEditMode){
            outState.putSerializable(Constants.STATE_PARCELLE,parceltoSave );
            outState.putBoolean(Constants.STATE_IS_EDIT_MODE, true);
        }else
            outState.putSerializable(Constants.STATE_PARCELLE,parceltoSave );
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restore old State
        isEditMode = savedInstanceState.getBoolean(Constants.STATE_IS_EDIT_MODE);
        parcel = null;
        parcel = (Parcelle) savedInstanceState.getSerializable(Constants.STATE_PARCELLE);
        if (parcel != null)
            onDisplayUi(parcel, false, true);
    }

    @Override
    public void onConnected() {
        super.onConnected();
    }

    @Override
    protected void onDestroy() {
        removeAsynTasks();
        super.onDestroy();
    }

    @Override
    public void onNotConnected() {
        super.onNotConnected();
    }
}

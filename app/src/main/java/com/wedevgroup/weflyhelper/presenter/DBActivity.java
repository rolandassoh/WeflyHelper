package com.wedevgroup.weflyhelper.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.LoginActivity;
import com.wedevgroup.weflyhelper.activity.MainActivity;
import com.wedevgroup.weflyhelper.model.Culture;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.model.CultureType;
import com.wedevgroup.weflyhelper.model.Region;
import com.wedevgroup.weflyhelper.task.ParcelleCreateTask;
import com.wedevgroup.weflyhelper.task.ParcelleDeleteTask;
import com.wedevgroup.weflyhelper.task.ParcelleEditTask;
import com.wedevgroup.weflyhelper.task.ParcellePostItemTask;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.AppController;

import java.util.ArrayList;

/**
 * Created by admin on 27/03/2018.
 */

public class DBActivity extends BaseActivity implements ParcelleCreateTask.OnParcelleSaveCompleteListener, ParcelleDeleteTask.OnParcelleDeleteListener, ParcellePostItemTask.OnParcelleSendListener, ParcelleEditTask.OnParcelleEditListener{

    private OnListChangeListener chListner;
    private Parcelle oldParcel = null;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBasePresenter.init(this);
    }


    public void removeParcelle(@NonNull Parcelle parcelle, @NonNull final View v ){
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcelleDeleteTask task = new ParcelleDeleteTask(this, parcelle);
                task.setOnParcelleDeleteListener(this, v);
                task.execute();
            }
        }

    }


    public void sendParcelle(@NonNull Parcelle parcelle, @NonNull final View v, @NonNull final Context ctx){
        oldParcel = parcelle;
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcellePostItemTask task = new ParcellePostItemTask(parcelle, ctx);
                task.setOnParcelleSendListener(this, v);
                task.execute();
            }
        }

    }

    public void removeParcelle(@NonNull Parcelle parcelle){
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcelleDeleteTask task = new ParcelleDeleteTask(this, parcelle);
                task.execute();
            }
        }

    }

    public void removeParcelle(@NonNull final Parcelle parcelle, @NonNull final DBActivity act, @NonNull final View v){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(
                act);
        dialog.setTitle("Suppression");
        dialog.setMessage("Êtes-vous sûr de vouloir supprimer cette parcelle");
        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (appController != null){
                    if (appController.isTokenAndUserOk(act)){
                        ParcelleDeleteTask task = new ParcelleDeleteTask(act, parcelle);
                        task.setOnParcelleDeleteListener(DBActivity.this, v);
                        task.execute();
                    }
                }

            }
        });
        dialog.setNegativeButton("Non", null);
        dialog.show();

    }

    protected void addParcelle(@NonNull Parcelle parcelle, @NonNull Bitmap image, boolean isSavedInTemp, @NonNull final View v){
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcelleCreateTask task = new ParcelleCreateTask(this, parcelle,image, isSavedInTemp );
                task.setOnParcelleSaveListener(this, v);
                task.execute();
            }
        }

    }

    protected void addParcelle(@NonNull Parcelle parcelle, boolean isSavedInTemp, @NonNull final View v){
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcelleCreateTask task = new ParcelleCreateTask(this, parcelle,isSavedInTemp );
                task.setOnParcelleSaveListener(this, v);
                task.execute();
            }
        }

    }

    protected void updateParcelle(@NonNull Parcelle parcelle, @NonNull final View v){
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcelleEditTask task = new ParcelleEditTask(this, parcelle);
                task.setOnParcelleEditListener(this, v);
                task.execute();
            }
        }

    }
    protected void updateParcelle(@NonNull Parcelle parcelle, boolean canDisplayToast, @NonNull final View v, boolean isFromTemp){
        if (appController != null){
            if (appController.isTokenAndUserOk(getApplicationContext())){
                ParcelleEditTask task = new ParcelleEditTask(this, parcelle, canDisplayToast, isFromTemp);
                task.setOnParcelleEditListener(this, v);
                task.execute();
            }
        }

    }

    protected @NonNull ArrayList<Culture> getCultures(final Context ctx ){
        ArrayList<Culture> list = new ArrayList<Culture>();
        try {
            list.addAll(DataBasePresenter.getInstance().getCultures(ctx));
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    protected @NonNull ArrayList<CultureType> getCultureType(final Context ctx ){
        ArrayList<CultureType> list = new ArrayList<CultureType>();
        try{
            list.addAll(DataBasePresenter.getInstance().getCultureTypes(ctx));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    protected @NonNull ArrayList<Region> getRegions(final  Context ctx){
        ArrayList<Region> list = new ArrayList<Region>();
        try {
            list.addAll(DataBasePresenter.getInstance().getRegions(ctx));
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            DataBasePresenter.getInstance().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected @NonNull ArrayList<Parcelle> getParcelles(){
        ArrayList<Parcelle> list = new ArrayList<Parcelle>();
        try{
            list.addAll(DataBasePresenter.getInstance().getParcelles());
            DataBasePresenter.getInstance().close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    protected void onDiplayParcelleDetails(@NonNull Activity act, @NonNull final Parcelle p){

        Intent formIntent = new Intent(act, MainActivity.class);
        formIntent.putExtra("showDetail", true);
        Bundle bundle = new Bundle();
        bundle.putSerializable("parcelObj", p);
        formIntent.putExtras(bundle);
        launchActivity(formIntent, act,false);
        AppController.clearDestroyList();
    }

    protected void cleanDB(){
        try {
            DataBasePresenter.getInstance().resetDB(this);
            Log.v(Constants.APP_NAME, TAG + " CLEAR DB DONE");
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(Constants.APP_NAME, TAG + " CAN'T CLEAR DB");

        }
    }


    @Override
    public void onSaveError(@NonNull Parcelle p, @NonNull View view) {
        Utils.showToast(DBActivity.this, R.string.parcel_saved_error, view);
    }

    @Override
    public void onSaveSucces(@NonNull Parcelle p, @NonNull View view) {
        Utils.showToast(DBActivity.this, R.string.parcel_saved, view);
    }


    @Override
    public void onDeleteError(@NonNull final Parcelle p, @NonNull View view) {
        Utils.showToast(DBActivity.this, R.string.parcel_deleted_error, view);
    }

    @Override
    public void onDeleteSucces(@NonNull final Parcelle p, @NonNull View view) {
        Utils.showToast(DBActivity.this, R.string.parcel_deleted, view);
    }

    @Override
    public void onSendError(@NonNull Parcelle p, @NonNull View view, boolean many) {
        if (many)
            Utils.showToast(DBActivity.this, R.string.parcel_uploaded_errors, view);
        else
            Utils.showToast(DBActivity.this, R.string.parcel_uploaded_error, view);

    }

    @Override
    public void onSendSucces(@NonNull Parcelle p, @NonNull View view) {
        Utils.showToast(DBActivity.this, R.string.parcel_uploaded, view);
    }

    @Override
    public void onEditError(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast) {
        if (canDisplayToast)
            Utils.showToast(DBActivity.this, R.string.parcel_updated_error, view);
    }

    @Override
    public void onEditSucces(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast) {
        if (canDisplayToast)
            Utils.showToast(DBActivity.this, R.string.parcel_updated, view);
    }


    public void setOnListChangeListener(@NonNull OnListChangeListener listener) {
        this.chListner = listener;
    }

    public static interface OnListChangeListener {
        void onParcelleAdded(@NonNull final Parcelle p);
        void onParcelleEdited(@NonNull final Parcelle oldParcel, @NonNull final Parcelle newParcel );
        void onParcelleDeleted(@NonNull final Parcelle p);
    }

}

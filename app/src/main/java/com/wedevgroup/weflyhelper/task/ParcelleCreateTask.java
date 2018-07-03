package com.wedevgroup.weflyhelper.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.presenter.DataBasePresenter;
import com.wedevgroup.weflyhelper.utils.AppController;

import dmax.dialog.SpotsDialog;

/**
 * Created by admin on 02/04/2018.
 */

public class ParcelleCreateTask extends AsyncTask<Void, Integer, Boolean> {
    Parcelle parcelle;
    Bitmap image;
    private AlertDialog dialog;
    private Activity act;
    private String TAG = getClass().getSimpleName();
    private boolean isTempAvai;
    private OnParcelleSaveCompleteListener listener;
    public View v;

    public ParcelleCreateTask(@NonNull final Activity activity, @NonNull Parcelle parcelle, @NonNull Bitmap image, boolean fromTemp){
        this.parcelle = parcelle;
        this.act = activity;
        this.image = image;
        this.isTempAvai = fromTemp;
        AppController.addTask(this);
    }

    public ParcelleCreateTask(@NonNull final Activity activity, @NonNull Parcelle parcelle, boolean fromTemp){
        this.parcelle = parcelle;
        this.act = activity;
        this.isTempAvai = fromTemp;
        AppController.addTask(this);
    }
    @Override
    protected void onPreExecute() {
        try {
            dialog = new SpotsDialog(act, R.style.SpotAlertDialog);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (image == null){
                DataBasePresenter.getInstance().addParcelle(parcelle, act, isTempAvai, false);
                DataBasePresenter.getInstance().close();
                return true;
            }else{
                if (DataBasePresenter.getInstance().addParcelle(parcelle, act, image, isTempAvai)){
                    DataBasePresenter.getInstance().close();
                    image.recycle();
                    image = null;
                    return true;
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean isOk) {
        if(isCancelled())
            return;
        try {
            dialog.dismiss();
            notifyOnSavingDoneListener(isOk, parcelle);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setOnParcelleSaveListener(@NonNull OnParcelleSaveCompleteListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;

    }

    public static interface OnParcelleSaveCompleteListener {
        void onSaveError(@NonNull Parcelle p, @NonNull View view);
        void onSaveSucces(@NonNull Parcelle p, @NonNull View view);
    }

    private void notifyOnSavingDoneListener(boolean isDone, @NonNull Parcelle parcel) {
        if (listener != null  && v != null){
            try {
                if (isDone)
                    listener.onSaveSucces(parcel, v);
                else
                    listener.onSaveError(parcel, v);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

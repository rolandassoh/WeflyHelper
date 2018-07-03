package com.wedevgroup.weflyhelper.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.presenter.DataBasePresenter;
import com.wedevgroup.weflyhelper.utils.AppController;

import dmax.dialog.SpotsDialog;

/**
 * Created by admin on 02/04/2018.
 */

public class ParcelleEditTask extends AsyncTask<Void, Integer, Boolean> {
    Parcelle parcelle;
    private AlertDialog dialog;
    private Activity act;
    private boolean canDisplayToast;
    private OnParcelleEditListener listener;
    private View v;
    private boolean isTempAvai;
    private boolean isFromApi;
    private String TAG = getClass().getSimpleName();

    public ParcelleEditTask(@NonNull final Activity activity, @NonNull Parcelle parcelle, boolean canDisplayToast){
        this.canDisplayToast = canDisplayToast;
        this.parcelle = parcelle;
        this.act = activity;
        AppController.addTask(this);
    }
    public ParcelleEditTask(@NonNull final Activity activity, @NonNull Parcelle parcelle, boolean canDisplayToast, boolean fromTemp){
        this.canDisplayToast = canDisplayToast;
        this.parcelle = parcelle;
        this.act = activity;
        this.isTempAvai = fromTemp;
    }

    public ParcelleEditTask(@NonNull final Activity activity, @NonNull Parcelle parcelle){
        this.parcelle = parcelle;
        this.act = activity;
        this.isFromApi = true;
        AppController.addTask(this);
    }

    @Override
    protected void onPreExecute() {
        try {
            dialog = new SpotsDialog(act, R.style.SpotAlertDialog);
            dialog.show();
            TextView title = dialog.findViewById(R.id.dmax_spots_title);
            title.setText(act.getString(R.string.loading_update));
        }catch ( Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try{

            if (isFromApi){
                // Save Without Image
                if (DataBasePresenter.getInstance().updateParcelleFromApi(parcelle))
                    DataBasePresenter.getInstance().close();
            }else {
                if (DataBasePresenter.getInstance().updateParcelle(parcelle,act, true))
                    DataBasePresenter.getInstance().close();
            }
            return true;
        }catch (Exception e){
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
            notifyOnParcelleEditListener(isOk, parcelle);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setOnParcelleEditListener(@NonNull OnParcelleEditListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;

    }

    public static interface OnParcelleEditListener {
        void onEditError(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast);
        void onEditSucces(@NonNull Parcelle p, @NonNull View view, boolean canDisplayToast);
    }

    private void notifyOnParcelleEditListener(boolean isDone, @NonNull Parcelle parcel) {
        if (listener != null && v != null){
            try {
                if (isDone)
                    listener.onEditSucces(parcel, v, canDisplayToast);
                else
                    listener.onEditError(parcel, v, canDisplayToast);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

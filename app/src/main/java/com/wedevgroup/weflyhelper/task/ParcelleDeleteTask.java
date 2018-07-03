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

public class ParcelleDeleteTask extends AsyncTask<Void, Integer, Boolean>{
    Parcelle mParcelle;
    private AlertDialog dialog;
    private Activity act;
    private OnParcelleDeleteListener listener;
    private View v;
    private String TAG = getClass().getSimpleName();

    public ParcelleDeleteTask(@NonNull final Activity activity, @NonNull Parcelle parcelle){
        this.mParcelle = parcelle;
        this.act = activity;
        AppController.addTask(this);
    }
    @Override
    protected void onPreExecute() {
        try {
            dialog = new SpotsDialog(act, R.style.SpotAlertDialog);
            dialog.show();
            TextView title = (TextView) dialog.findViewById(R.id.dmax_spots_title);
            title.setText(act.getString(R.string.loading_delete));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (DataBasePresenter.getInstance().deleteParcelle(mParcelle,act)){
                DataBasePresenter.getInstance().close();
                return true;
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
            notifyOnParcelleDeleteListener(isOk, mParcelle);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void setOnParcelleDeleteListener(@NonNull OnParcelleDeleteListener listener, @NonNull View view) {
        this.listener = listener;
        this.v = view;

    }

    public static interface OnParcelleDeleteListener {
        void onDeleteError(@NonNull final Parcelle p, @NonNull View view);
        void onDeleteSucces(@NonNull final Parcelle p, @NonNull View view);
    }

    private void notifyOnParcelleDeleteListener(boolean isDone, @NonNull Parcelle parcel) {
        if (listener != null && v != null){
            try {
                if (isDone)
                    listener.onDeleteSucces(parcel, v);
                else
                    listener.onDeleteError(parcel, v);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}

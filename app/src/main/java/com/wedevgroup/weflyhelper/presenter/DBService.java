package com.wedevgroup.weflyhelper.presenter;

import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;

import com.wedevgroup.weflyhelper.model.Parcelle;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 04/04/2018.
 */

public class DBService extends BaseService {

    protected @NonNull DataBasePresenter getDBManager(){
        try {
            if (DataBasePresenter.getInstance() != null)
                return DataBasePresenter.getInstance();
        } catch (Exception e){
        }
        DataBasePresenter.init(this);
        return DataBasePresenter.getInstance();

    }

    protected boolean updateDb(@NonNull CopyOnWriteArrayList<Parcelle> parcelToDelete, @NonNull String newListToUpdate,  final Context ctx){

        return getDBManager().updateDataBase(parcelToDelete, newListToUpdate,  ctx);
    }


    protected boolean saveOnDb(@NonNull CopyOnWriteArrayList<Parcelle> parcelToDelete, @NonNull String newListToCreate, final Context ctx){
        return getDBManager().saveCreated(parcelToDelete, newListToCreate, ctx);
    }
}

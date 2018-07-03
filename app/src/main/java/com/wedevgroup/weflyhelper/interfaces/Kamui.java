package com.wedevgroup.weflyhelper.interfaces;

import android.support.annotation.NonNull;

import com.wedevgroup.weflyhelper.model.Parcelle;

/**
 * Created by admin on 09/04/2018.
 */

public interface Kamui {

    void shouldNotifDataSetChange();
    void onParcelleDeleteError(@NonNull Parcelle parcelle);
    void onParcelleDeleteDone();
}

package com.google.android.gms.maps;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.internal.zzf;
import com.wedevgroup.weflyhelper.model.Point;
import com.wedevgroup.weflyhelper.utils.Constants;

import java.io.Serializable;

/**
 * Created by admin on 27/03/2018.
 */

public class PointCustom implements Serializable {
    private static final long serialVersionUID = 10L;
    private Marker marker;
    private Point point;
    private int idOnMap;

    public PointCustom() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public @Nullable Marker getMarker() throws Exception {
        //if (marker == null)
            // do start
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Point getPoint() {
        if (point == null)
            point = new Point();
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getIdOnMap() {
        return idOnMap;
    }

    public @NonNull String getIdOnMapAsString() {
        return String.valueOf(idOnMap);
    }

    public void setIdOnMap(int idOnMap) {
        this.idOnMap = idOnMap;
    }
}

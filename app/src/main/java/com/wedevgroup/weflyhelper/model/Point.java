package com.wedevgroup.weflyhelper.model;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wedevgroup.weflyhelper.utils.Constants;

import java.io.Serializable;

/**
 * Created by admin on 27/03/2018.
 */

public class Point implements Serializable {
    private static final long serialVersionUID = 10L;
    private int pointId;
    private int parcelleId;
    private int rang;
    private Double latitude;
    private Double longitude;
    private boolean isReference;
    private boolean isCenter;

    public Point(int id, int parcelleId, Double latitude, Double longitude, boolean isReference, int rang) {
        pointId = id;
        this.parcelleId = parcelleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isReference = isReference;
        this.rang = rang;
    }

    public Point() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public Double getLatitude() {
        if(latitude == null)
            latitude = Constants.DOUBLE_NULL;
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        if(longitude == null)
            longitude = Constants.DOUBLE_NULL;
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isReference() {
        return isReference;
    }

    public void setReference(boolean reference) {
        isReference = reference;
    }

    public int getIsReferenceAsInt(){
        int bool = (isReference)? 1 : 0;
        return  bool;
    }
    public int getIsCenterAsInt(){
        int bool = (isCenter)? 1 : 0;
        return  bool;
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public LatLng getLatLng() {
        LatLng latLng = new LatLng(getLatitude(), getLongitude());
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
    }

    public boolean isCenter() {
        return isCenter;
    }



    public void setCenter(boolean center) {
        isCenter = center;
    }

    public void setCenter(int center) {
        isCenter = center == 1;
    }

    public void setReference(int reference) {
        isReference = reference == 1;
    }

    public int getParcelleId() {
        return parcelleId;
    }

    public void setParcelleId(int parcelleId) {
        this.parcelleId = parcelleId;
    }
}

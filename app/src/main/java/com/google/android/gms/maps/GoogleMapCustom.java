// 
// Decompiled by Procyon v0.5.29
// 

package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.ILocationSourceDelegate;
import com.google.android.gms.maps.internal.zzaa;
import com.google.android.gms.maps.internal.zzag;
import com.google.android.gms.maps.internal.zzb;
import com.google.android.gms.maps.internal.zze;
import com.google.android.gms.maps.internal.zzg;
import com.google.android.gms.maps.internal.zzi;
import com.google.android.gms.maps.internal.zzj;
import com.google.android.gms.maps.internal.zzk;
import com.google.android.gms.maps.internal.zzl;
import com.google.android.gms.maps.internal.zzm;
import com.google.android.gms.maps.internal.zzn;
import com.google.android.gms.maps.internal.zzo;
import com.google.android.gms.maps.internal.zzp;
import com.google.android.gms.maps.internal.zzq;
import com.google.android.gms.maps.internal.zzr;
import com.google.android.gms.maps.internal.zzs;
import com.google.android.gms.maps.internal.zzu;
import com.google.android.gms.maps.internal.zzv;
import com.google.android.gms.maps.internal.zzw;
import com.google.android.gms.maps.internal.zzx;
import com.google.android.gms.maps.internal.zzy;
import com.google.android.gms.maps.internal.zzz;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import com.google.android.gms.maps.model.internal.zzc;
import com.google.android.gms.maps.model.internal.zzd;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.maps.model.internal.zzh;
import com.wedevgroup.weflyhelper.utils.Constants;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public final class GoogleMapCustom
{
    public static final int MAP_TYPE_NONE = 0;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_SATELLITE = 2;
    public static final int MAP_TYPE_TERRAIN = 3;
    public static final int MAP_TYPE_HYBRID = 4;
    private final IGoogleMapDelegate zzbmY;
    private UiSettings zzbmZ;
    private String TAG = getClass().getSimpleName();
    private ArrayList<PointCustom> pList = new ArrayList<PointCustom>();
    
    protected GoogleMapCustom(final IGoogleMapDelegate googleMapDelegate) {
        this.zzbmY = (IGoogleMapDelegate) zzac.zzw((Object)googleMapDelegate);
    }
    
    public final CameraPosition getCameraPosition() {
        try {
            return this.zzbmY.getCameraPosition();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final float getMaxZoomLevel() {
        try {
            return this.zzbmY.getMaxZoomLevel();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final float getMinZoomLevel() {
        try {
            return this.zzbmY.getMinZoomLevel();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void moveCamera(final CameraUpdate cameraUpdate) {
        try {
            this.zzbmY.moveCamera(cameraUpdate.zzIy());
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void animateCamera(final CameraUpdate cameraUpdate) {
        try {
            this.zzbmY.animateCamera(cameraUpdate.zzIy());
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void animateCamera(final CameraUpdate cameraUpdate, final CancelableCallback cancelableCallback) {
        try {
            this.zzbmY.animateCameraWithCallback(cameraUpdate.zzIy(), (cancelableCallback == null) ? null : new zza(cancelableCallback));
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void animateCamera(final CameraUpdate cameraUpdate, final int n, final CancelableCallback cancelableCallback) {
        try {
            this.zzbmY.animateCameraWithDurationAndCallback(cameraUpdate.zzIy(), n, (cancelableCallback == null) ? null : new zza(cancelableCallback));
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void stopAnimation() {
        try {
            this.zzbmY.stopAnimation();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final Polyline addPolyline(final PolylineOptions polylineOptions) {
        try {
            return new Polyline(this.zzbmY.addPolyline(polylineOptions));
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final Polygon addPolygon(final PolygonOptions polygonOptions) {
        try {
            return new Polygon(this.zzbmY.addPolygon(polygonOptions));
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final Circle addCircle(final CircleOptions circleOptions) {
        try {
            return new Circle(this.zzbmY.addCircle(circleOptions));
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final PointCustom addMarker(final @NonNull MarkerOptions markerOptions, final @NonNull CopyOnWriteArrayList<PointCustom> points, @NonNull PointCustom p) {
        try {
            final zzf addMarker = this.zzbmY.addMarker(markerOptions);
            pList.clear();
            pList.addAll(points);
            if (addMarker != null) {
                p.setMarker(new Marker(addMarker));
                return p;
            }
            return null;
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final GroundOverlay addGroundOverlay(final GroundOverlayOptions groundOverlayOptions) {
        try {
            final zzc addGroundOverlay = this.zzbmY.addGroundOverlay(groundOverlayOptions);
            if (addGroundOverlay != null) {
                return new GroundOverlay(addGroundOverlay);
            }
            return null;
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final TileOverlay addTileOverlay(final TileOverlayOptions tileOverlayOptions) {
        try {
            final zzh addTileOverlay = this.zzbmY.addTileOverlay(tileOverlayOptions);
            if (addTileOverlay != null) {
                return new TileOverlay(addTileOverlay);
            }
            return null;
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void clear() {
        try {
            this.zzbmY.clear();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public IndoorBuilding getFocusedBuilding() {
        try {
            final zzd focusedBuilding = this.zzbmY.getFocusedBuilding();
            if (focusedBuilding != null) {
                return new IndoorBuilding(focusedBuilding);
            }
            return null;
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnIndoorStateChangeListener(final OnIndoorStateChangeListener onIndoorStateChangeListener) {
        try {
            if (onIndoorStateChangeListener == null) {
                this.zzbmY.setOnIndoorStateChangeListener(null);
            }
            else {
                this.zzbmY.setOnIndoorStateChangeListener(new zzl.zza() {
                    public void onIndoorBuildingFocused() {
                        onIndoorStateChangeListener.onIndoorBuildingFocused();
                    }
                    
                    public void zza(final zzd zzd) {
                        onIndoorStateChangeListener.onIndoorLevelActivated(new IndoorBuilding(zzd));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final int getMapType() {
        try {
            return this.zzbmY.getMapType();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setMapType(final int mapType) {
        try {
            this.zzbmY.setMapType(mapType);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final boolean isTrafficEnabled() {
        try {
            return this.zzbmY.isTrafficEnabled();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setTrafficEnabled(final boolean trafficEnabled) {
        try {
            this.zzbmY.setTrafficEnabled(trafficEnabled);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final boolean isIndoorEnabled() {
        try {
            return this.zzbmY.isIndoorEnabled();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final boolean setIndoorEnabled(final boolean indoorEnabled) {
        try {
            return this.zzbmY.setIndoorEnabled(indoorEnabled);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final boolean isBuildingsEnabled() {
        try {
            return this.zzbmY.isBuildingsEnabled();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setBuildingsEnabled(final boolean buildingsEnabled) {
        try {
            this.zzbmY.setBuildingsEnabled(buildingsEnabled);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final boolean isMyLocationEnabled() {
        try {
            return this.zzbmY.isMyLocationEnabled();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    @RequiresPermission(anyOf = { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" })
    public final void setMyLocationEnabled(final boolean myLocationEnabled) {
        try {
            this.zzbmY.setMyLocationEnabled(myLocationEnabled);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    @Deprecated
    public final Location getMyLocation() {
        try {
            return this.zzbmY.getMyLocation();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setLocationSource(final LocationSource locationSource) {
        try {
            if (locationSource == null) {
                this.zzbmY.setLocationSource(null);
            }
            else {
                this.zzbmY.setLocationSource(new ILocationSourceDelegate.zza() {
                    public void activate(final zzp zzp) {
                        locationSource.activate(new LocationSource.OnLocationChangedListener() {
                            @Override
                            public void onLocationChanged(final Location location) {
                                try {
                                    zzp.zze(location);
                                }
                                catch (RemoteException ex) {
                                    throw new RuntimeRemoteException(ex);
                                }
                            }
                        });
                    }
                    
                    public void deactivate() {
                        locationSource.deactivate();
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final UiSettings getUiSettings() {
        try {
            if (this.zzbmZ == null) {
                this.zzbmZ = new UiSettings(this.zzbmY.getUiSettings());
            }
            return this.zzbmZ;
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final Projection getProjection() {
        try {
            return new Projection(this.zzbmY.getProjection());
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    @Deprecated
    public final void setOnCameraChangeListener(final OnCameraChangeListener onCameraChangeListener) {
        try {
            if (onCameraChangeListener == null) {
                this.zzbmY.setOnCameraChangeListener(null);
            }
            else {
                this.zzbmY.setOnCameraChangeListener(new zze.zza() {
                    public void onCameraChange(final CameraPosition cameraPosition) {
                        onCameraChangeListener.onCameraChange(cameraPosition);
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnCameraMoveStartedListener(final OnCameraMoveStartedListener onCameraMoveStartedListener) {
        try {
            if (onCameraMoveStartedListener == null) {
                this.zzbmY.setOnCameraMoveStartedListener(null);
            }
            else {
                this.zzbmY.setOnCameraMoveStartedListener(new zzi.zza() {
                    public void onCameraMoveStarted(final int n) {
                        onCameraMoveStartedListener.onCameraMoveStarted(n);
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnCameraMoveListener(final OnCameraMoveListener onCameraMoveListener) {
        try {
            if (onCameraMoveListener == null) {
                this.zzbmY.setOnCameraMoveListener(null);
            }
            else {
                this.zzbmY.setOnCameraMoveListener(new com.google.android.gms.maps.internal.zzh.zza() {
                    public void onCameraMove() {
                        onCameraMoveListener.onCameraMove();
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnCameraMoveCanceledListener(final OnCameraMoveCanceledListener onCameraMoveCanceledListener) {
        try {
            if (onCameraMoveCanceledListener == null) {
                this.zzbmY.setOnCameraMoveCanceledListener(null);
            }
            else {
                this.zzbmY.setOnCameraMoveCanceledListener(new zzg.zza() {
                    public void onCameraMoveCanceled() {
                        onCameraMoveCanceledListener.onCameraMoveCanceled();
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnCameraIdleListener(final OnCameraIdleListener onCameraIdleListener) {
        try {
            if (onCameraIdleListener == null) {
                this.zzbmY.setOnCameraIdleListener(null);
            }
            else {
                this.zzbmY.setOnCameraIdleListener(new com.google.android.gms.maps.internal.zzf.zza() {
                    public void onCameraIdle() {
                        onCameraIdleListener.onCameraIdle();
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnMapClickListener(final OnMapClickListener onMapClickListener) {
        try {
            if (onMapClickListener == null) {
                this.zzbmY.setOnMapClickListener(null);
            }
            else {
                this.zzbmY.setOnMapClickListener(new zzq.zza() {
                    public void onMapClick(final LatLng latLng) {
                        onMapClickListener.onMapClick(latLng);
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnMapLongClickListener(final OnMapLongClickListener onMapLongClickListener) {
        try {
            if (onMapLongClickListener == null) {
                this.zzbmY.setOnMapLongClickListener(null);
            }
            else {
                this.zzbmY.setOnMapLongClickListener(new zzs.zza() {
                    public void onMapLongClick(final LatLng latLng) {
                        onMapLongClickListener.onMapLongClick(latLng);
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnMarkerClickListener(final OnMarkerClickListener onMarkerClickListener) {
        try {
            if (onMarkerClickListener == null) {
                this.zzbmY.setOnMarkerClickListener(null);
            }
            else {
                this.zzbmY.setOnMarkerClickListener(new zzu.zza() {
                    public boolean zza(final zzf zzf) {
                        return onMarkerClickListener.onMarkerClick(new PointCustom());
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnMarkerDragListener(final OnMarkerDragListener onMarkerDragListener) {
        try {
            if (onMarkerDragListener == null) {
                this.zzbmY.setOnMarkerDragListener(null);
            }
            else {
                this.zzbmY.setOnMarkerDragListener(new zzv.zza() {
                    public void zzb(final zzf zzf) throws RemoteException {
                        onMarkerDragListener.onMarkerDragStart(getPoint(zzf));
                    }
                    
                    public void zzc(final zzf zzf) throws RemoteException {

                        onMarkerDragListener.onMarkerDragEnd(getPoint(zzf));
                    }
                    
                    public void zzd(final zzf zzf) throws RemoteException {
                        onMarkerDragListener.onMarkerDrag(getPoint(zzf));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }

    private @NonNull PointCustom getPoint(@NonNull final zzf zzf) throws RemoteException {
        PointCustom p = null;
        if (pList != null && pList.size() > 0 && !zzf.getSnippet().equalsIgnoreCase("")){
            for (PointCustom pm: pList){
                try {
                    if (pm.getIdOnMap() == Integer.valueOf(zzf.getSnippet())){
                        p = pm;
                        p.getPoint().setLatLng(zzf.getPosition());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if (p == null)
            p = new PointCustom();

        return  p;
    }

    public final void setOnInfoWindowClickListener(final OnInfoWindowClickListener onInfoWindowClickListener) {
        try {
            if (onInfoWindowClickListener == null) {
                this.zzbmY.setOnInfoWindowClickListener(null);
            }
            else {
                this.zzbmY.setOnInfoWindowClickListener(new zzm.zza() {
                    public void zze(final zzf zzf) {
                        onInfoWindowClickListener.onInfoWindowClick(new Marker(zzf));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnInfoWindowLongClickListener(final OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
        try {
            if (onInfoWindowLongClickListener == null) {
                this.zzbmY.setOnInfoWindowLongClickListener(null);
            }
            else {
                this.zzbmY.setOnInfoWindowLongClickListener(new zzo.zza() {
                    public void zzf(final zzf zzf) {
                        onInfoWindowLongClickListener.onInfoWindowLongClick(new Marker(zzf));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnInfoWindowCloseListener(final OnInfoWindowCloseListener onInfoWindowCloseListener) {
        try {
            if (onInfoWindowCloseListener == null) {
                this.zzbmY.setOnInfoWindowCloseListener(null);
            }
            else {
                this.zzbmY.setOnInfoWindowCloseListener(new zzn.zza() {
                    public void zzg(final zzf zzf) {
                        onInfoWindowCloseListener.onInfoWindowClose(new Marker(zzf));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setInfoWindowAdapter(final InfoWindowAdapter infoWindowAdapter) {
        try {
            if (infoWindowAdapter == null) {
                this.zzbmY.setInfoWindowAdapter(null);
            }
            else {
                this.zzbmY.setInfoWindowAdapter(new com.google.android.gms.maps.internal.zzd.zza() {
                    public com.google.android.gms.dynamic.zzd zzh(final zzf zzf) {
                        return com.google.android.gms.dynamic.zze.zzA((Object)infoWindowAdapter.getInfoWindow(new Marker(zzf)));
                    }
                    
                    public com.google.android.gms.dynamic.zzd zzi(final zzf zzf) {
                        return com.google.android.gms.dynamic.zze.zzA((Object)infoWindowAdapter.getInfoContents(new Marker(zzf)));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    @Deprecated
    public final void setOnMyLocationChangeListener(final OnMyLocationChangeListener onMyLocationChangeListener) {
        try {
            if (onMyLocationChangeListener == null) {
                this.zzbmY.setOnMyLocationChangeListener(null);
            }
            else {
                this.zzbmY.setOnMyLocationChangeListener(new zzx.zza() {
                    public void zzF(final com.google.android.gms.dynamic.zzd zzd) {
                        onMyLocationChangeListener.onMyLocationChange((Location)com.google.android.gms.dynamic.zze.zzE(zzd));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnMyLocationButtonClickListener(final OnMyLocationButtonClickListener onMyLocationButtonClickListener) {
        try {
            if (onMyLocationButtonClickListener == null) {
                this.zzbmY.setOnMyLocationButtonClickListener(null);
            }
            else {
                this.zzbmY.setOnMyLocationButtonClickListener(new zzw.zza() {
                    public boolean onMyLocationButtonClick() throws RemoteException {
                        return onMyLocationButtonClickListener.onMyLocationButtonClick();
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public void setOnMapLoadedCallback(final OnMapLoadedCallback onMapLoadedCallback) {
        try {
            if (onMapLoadedCallback == null) {
                this.zzbmY.setOnMapLoadedCallback(null);
            }
            else {
                this.zzbmY.setOnMapLoadedCallback(new zzr.zza() {
                    public void onMapLoaded() throws RemoteException {
                        onMapLoadedCallback.onMapLoaded();
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnGroundOverlayClickListener(final OnGroundOverlayClickListener onGroundOverlayClickListener) {
        try {
            if (onGroundOverlayClickListener == null) {
                this.zzbmY.setOnGroundOverlayClickListener(null);
            }
            else {
                this.zzbmY.setOnGroundOverlayClickListener(new zzk.zza() {
                    public void zza(final zzc zzc) {
                        onGroundOverlayClickListener.onGroundOverlayClick(new GroundOverlay(zzc));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnCircleClickListener(final OnCircleClickListener onCircleClickListener) {
        try {
            if (onCircleClickListener == null) {
                this.zzbmY.setOnCircleClickListener(null);
            }
            else {
                this.zzbmY.setOnCircleClickListener(new zzj.zza() {
                    public void zza(final com.google.android.gms.maps.model.internal.zzb zzb) {
                        onCircleClickListener.onCircleClick(new Circle(zzb));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnPolygonClickListener(final OnPolygonClickListener onPolygonClickListener) {
        try {
            if (onPolygonClickListener == null) {
                this.zzbmY.setOnPolygonClickListener(null);
            }
            else {
                this.zzbmY.setOnPolygonClickListener(new zzz.zza() {
                    public void zza(final com.google.android.gms.maps.model.internal.zzg zzg) {
                        onPolygonClickListener.onPolygonClick(new Polygon(zzg));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnPolylineClickListener(final OnPolylineClickListener onPolylineClickListener) {
        try {
            if (onPolylineClickListener == null) {
                this.zzbmY.setOnPolylineClickListener(null);
            }
            else {
                this.zzbmY.setOnPolylineClickListener(new zzaa.zza() {
                    public void zza(final IPolylineDelegate polylineDelegate) {
                        onPolylineClickListener.onPolylineClick(new Polyline(polylineDelegate));
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void snapshot(final SnapshotReadyCallback snapshotReadyCallback) {
        this.snapshot(snapshotReadyCallback, null);
    }
    
    public final void snapshot(final SnapshotReadyCallback snapshotReadyCallback, final Bitmap bitmap) {
        final com.google.android.gms.dynamic.zze zze = (com.google.android.gms.dynamic.zze)((bitmap != null) ? com.google.android.gms.dynamic.zze.zzA((Object)bitmap) : null);
        try {
            this.zzbmY.snapshot(new zzag.zza() {
                public void onSnapshotReady(final Bitmap bitmap) throws RemoteException {
                    snapshotReadyCallback.onSnapshotReady(bitmap);
                }
                
                public void zzG(final com.google.android.gms.dynamic.zzd zzd) throws RemoteException {
                    snapshotReadyCallback.onSnapshotReady((Bitmap)com.google.android.gms.dynamic.zze.zzE(zzd));
                }
            }, (com.google.android.gms.dynamic.zzd)zze);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setPadding(final int n, final int n2, final int n3, final int n4) {
        try {
            this.zzbmY.setPadding(n, n2, n3, n4);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setContentDescription(final String contentDescription) {
        try {
            this.zzbmY.setContentDescription(contentDescription);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public final void setOnPoiClickListener(final OnPoiClickListener onPoiClickListener) {
        try {
            if (onPoiClickListener == null) {
                this.zzbmY.setOnPoiClickListener(null);
            }
            else {
                this.zzbmY.setOnPoiClickListener(new zzy.zza() {
                    public void zza(final PointOfInterest pointOfInterest) throws RemoteException {
                        onPoiClickListener.onPoiClick(pointOfInterest);
                    }
                });
            }
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public boolean setMapStyle(final MapStyleOptions mapStyle) {
        try {
            return this.zzbmY.setMapStyle(mapStyle);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public void setMinZoomPreference(final float minZoomPreference) {
        try {
            this.zzbmY.setMinZoomPreference(minZoomPreference);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public void setMaxZoomPreference(final float maxZoomPreference) {
        try {
            this.zzbmY.setMaxZoomPreference(maxZoomPreference);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public void resetMinMaxZoomPreference() {
        try {
            this.zzbmY.resetMinMaxZoomPreference();
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }
    
    public void setLatLngBoundsForCameraTarget(final LatLngBounds latLngBoundsForCameraTarget) {
        try {
            this.zzbmY.setLatLngBoundsForCameraTarget(latLngBoundsForCameraTarget);
        }
        catch (RemoteException ex) {
            throw new RuntimeRemoteException(ex);
        }
    }

    public void updatePointsList(@NonNull CopyOnWriteArrayList<PointCustom> pointsUpdated) {
        pList.clear();
        pList.addAll(pointsUpdated);
    }

    public interface OnPoiClickListener
    {
        void onPoiClick(PointOfInterest p0);
    }
    
    private static final class zza extends zzb.zza
    {
        private final CancelableCallback zzbnz;
        
        zza(final CancelableCallback zzbnz) {
            this.zzbnz = zzbnz;
        }
        
        public void onFinish() {
            this.zzbnz.onFinish();
        }
        
        public void onCancel() {
            this.zzbnz.onCancel();
        }
    }
    
    public interface CancelableCallback
    {
        void onFinish();
        
        void onCancel();
    }
    
    public interface OnGroundOverlayClickListener
    {
        void onGroundOverlayClick(GroundOverlay p0);
    }
    
    public interface OnMapLoadedCallback
    {
        void onMapLoaded();
    }
    
    public interface OnMyLocationButtonClickListener
    {
        boolean onMyLocationButtonClick();
    }
    
    @Deprecated
    public interface OnMyLocationChangeListener
    {
        void onMyLocationChange(Location p0);
    }
    
    public interface InfoWindowAdapter
    {
        View getInfoWindow(Marker p0);
        
        View getInfoContents(Marker p0);
    }
    
    public interface SnapshotReadyCallback
    {
        void onSnapshotReady(Bitmap p0);
    }
    
    public interface OnInfoWindowCloseListener
    {
        void onInfoWindowClose(Marker p0);
    }
    
    public interface OnInfoWindowLongClickListener
    {
        void onInfoWindowLongClick(Marker p0);
    }
    
    public interface OnInfoWindowClickListener
    {
        void onInfoWindowClick(Marker p0);
    }
    
    public interface OnMarkerDragListener
    {
        void onMarkerDragStart(final PointCustom p0);
        
        void onMarkerDrag(final PointCustom p0);
        
        void onMarkerDragEnd(final PointCustom p0);
    }
    
    public interface OnMarkerClickListener
    {
        boolean onMarkerClick(PointCustom p0);
    }
    
    public interface OnPolylineClickListener
    {
        void onPolylineClick(Polyline p0);
    }
    
    public interface OnPolygonClickListener
    {
        void onPolygonClick(Polygon p0);
    }
    
    public interface OnCircleClickListener
    {
        void onCircleClick(Circle p0);
    }
    
    @Deprecated
    public interface OnCameraChangeListener
    {
        void onCameraChange(CameraPosition p0);
    }
    
    public interface OnCameraIdleListener
    {
        void onCameraIdle();
    }
    
    public interface OnCameraMoveCanceledListener
    {
        void onCameraMoveCanceled();
    }
    
    public interface OnCameraMoveListener
    {
        void onCameraMove();
    }
    
    public interface OnCameraMoveStartedListener
    {
        public static final int REASON_GESTURE = 1;
        public static final int REASON_API_ANIMATION = 2;
        public static final int REASON_DEVELOPER_ANIMATION = 3;
        
        void onCameraMoveStarted(int p0);
    }
    
    public interface OnMapLongClickListener
    {
        void onMapLongClick(LatLng p0);
    }
    
    public interface OnMapClickListener
    {
        void onMapClick(LatLng p0);
    }
    
    public interface OnIndoorStateChangeListener
    {
        void onIndoorBuildingFocused();
        
        void onIndoorLevelActivated(IndoorBuilding p0);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.google.android.gms.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzf;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.internal.zzt;
import com.google.android.gms.maps.model.RuntimeRemoteException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapViewCustom extends FrameLayout
{
    private final zzb zzbnV;
    
    public MapViewCustom(final Context context) {
        super(context);
        this.zzbnV = new zzb((ViewGroup)this, context, null);
        this.zzIM();
    }
    
    public MapViewCustom(final Context context, final AttributeSet set) {
        super(context, set);
        this.zzbnV = new zzb((ViewGroup)this, context, GoogleMapOptions.createFromAttributes(context, set));
        this.zzIM();
    }
    
    public MapViewCustom(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.zzbnV = new zzb((ViewGroup)this, context, GoogleMapOptions.createFromAttributes(context, set));
        this.zzIM();
    }
    
    public MapViewCustom(final Context context, final GoogleMapOptions googleMapOptions) {
        super(context);
        this.zzbnV = new zzb((ViewGroup)this, context, googleMapOptions);
        this.zzIM();
    }
    
    private void zzIM() {
        this.setClickable(true);
    }
    
    public final void onCreate(final Bundle bundle) {
        this.zzbnV.onCreate(bundle);
        if (this.zzbnV.zzAY() == null) {
            com.google.android.gms.dynamic.zza.zzb((FrameLayout)this);
        }
    }
    
    public final void onResume() {
        this.zzbnV.onResume();
    }
    
    public final void onPause() {
        this.zzbnV.onPause();
    }
    
    public final void onStart() {
        this.zzbnV.onStart();
    }
    
    public final void onStop() {
        this.zzbnV.onStop();
    }
    
    public final void onDestroy() {
        this.zzbnV.onDestroy();
    }
    
    public final void onLowMemory() {
        this.zzbnV.onLowMemory();
    }
    
    public final void onSaveInstanceState(final Bundle bundle) {
        this.zzbnV.onSaveInstanceState(bundle);
    }
    
    public void getMapAsync(final OnMapReadyCallBackCustom onMapReadyCallback) {
        zzac.zzdn("getMapAsync() must be called on the main thread");
        this.zzbnV.getMapAsync(onMapReadyCallback);
    }
    
    public final void onEnterAmbient(final Bundle bundle) {
        zzac.zzdn("onEnterAmbient() must be called on the main thread");
        this.zzbnV.onEnterAmbient(bundle);
    }
    
    public final void onExitAmbient() {
        zzac.zzdn("onExitAmbient() must be called on the main thread");
        this.zzbnV.onExitAmbient();
    }
    
    static class zzb extends com.google.android.gms.dynamic.zza<zza>
    {
        private final ViewGroup zzbnZ;
        private final Context mContext;
        protected zzf<zza> zzbnT;
        private final GoogleMapOptions zzboa;
        private final List<OnMapReadyCallBackCustom> zzbnU;
        
        zzb(final ViewGroup zzbnZ, final Context mContext, final GoogleMapOptions zzboa) {
            this.zzbnU = new ArrayList<OnMapReadyCallBackCustom>();
            this.zzbnZ = zzbnZ;
            this.mContext = mContext;
            this.zzboa = zzboa;
        }
        
        protected void zza(final zzf<zza> zzbnT) {
            this.zzbnT = zzbnT;
            this.zzIL();
        }
        
        public void zzIL() {
            if (this.zzbnT != null && this.zzAY() == null) {
                try {
                    MapsInitializer.initialize(this.mContext);
                    final IMapViewDelegate zza = zzai.zzbq(this.mContext).zza(zze.zzA((Object)this.mContext), this.zzboa);
                    if (zza == null) {
                        return;
                    }
                    this.zzbnT.zza(new zza(this.zzbnZ, zza));
                    final Iterator<OnMapReadyCallBackCustom> iterator = this.zzbnU.iterator();
                    while (iterator.hasNext()) {
                        ((zza)this.zzAY()).getMapAsync(iterator.next());
                    }
                    this.zzbnU.clear();
                }
                catch (RemoteException ex) {
                    throw new RuntimeRemoteException(ex);
                }
                catch (GooglePlayServicesNotAvailableException ex2) {}
            }
        }
        
        public void getMapAsync(final OnMapReadyCallBackCustom onMapReadyCallback) {
            if (this.zzAY() != null) {
                ((zza)this.zzAY()).getMapAsync(onMapReadyCallback);
            }
            else {
                this.zzbnU.add(onMapReadyCallback);
            }
        }
        
        public void onEnterAmbient(final Bundle bundle) {
            if (this.zzAY() != null) {
                ((zza)this.zzAY()).onEnterAmbient(bundle);
            }
        }
        
        public void onExitAmbient() {
            if (this.zzAY() != null) {
                ((zza)this.zzAY()).onExitAmbient();
            }
        }
    }
    
    static class zza implements MapLifecycleDelegateCustom
    {
        private final ViewGroup zzbnW;
        private final IMapViewDelegate zzbnX;
        private View zzbnY;
        
        public zza(final ViewGroup viewGroup, final IMapViewDelegate mapViewDelegate) {
            this.zzbnX = (IMapViewDelegate)zzac.zzw((Object)mapViewDelegate);
            this.zzbnW = (ViewGroup)zzac.zzw((Object)viewGroup);
        }
        
        public void onInflate(final Activity activity, final Bundle bundle, final Bundle bundle2) {
            throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
        }
        
        public void onCreate(final Bundle bundle) {
            try {
                this.zzbnX.onCreate(bundle);
                this.zzbnY = (View)zze.zzE(this.zzbnX.getView());
                this.zzbnW.removeAllViews();
                this.zzbnW.addView(this.zzbnY);
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
            throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
        }
        
        public void onStart() {
            try {
                this.zzbnX.onStart();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onResume() {
            try {
                this.zzbnX.onResume();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onPause() {
            try {
                this.zzbnX.onPause();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onStop() {
            try {
                this.zzbnX.onStop();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onDestroyView() {
            throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
        }
        
        public void onDestroy() {
            try {
                this.zzbnX.onDestroy();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onLowMemory() {
            try {
                this.zzbnX.onLowMemory();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onSaveInstanceState(final Bundle bundle) {
            try {
                this.zzbnX.onSaveInstanceState(bundle);
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        @Override
        public void getMapAsync(final OnMapReadyCallBackCustom onMapReadyCallback) {
            try {
                this.zzbnX.getMapAsync(new zzt.zza() {
                    public void zza(final IGoogleMapDelegate googleMapDelegate) throws RemoteException {
                        onMapReadyCallback.onMapReady(new GoogleMapCustom(googleMapDelegate));
                    }
                });
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onEnterAmbient(final Bundle bundle) {
            try {
                this.zzbnX.onEnterAmbient(bundle);
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
        
        public void onExitAmbient() {
            try {
                this.zzbnX.onExitAmbient();
            }
            catch (RemoteException ex) {
                throw new RuntimeRemoteException(ex);
            }
        }
    }
}

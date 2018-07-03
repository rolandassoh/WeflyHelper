package com.google.android.gms.maps;

import com.google.android.gms.dynamic.LifecycleDelegate;

/**
 * Created by admin on 24/05/2018.
 */

public interface MapLifecycleDelegateCustom extends LifecycleDelegate {
    void getMapAsync(OnMapReadyCallBackCustom var1);
}

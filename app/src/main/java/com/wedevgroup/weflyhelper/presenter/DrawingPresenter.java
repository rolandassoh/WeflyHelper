package com.wedevgroup.weflyhelper.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMapCustom;
import com.google.android.gms.maps.PointCustom;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.CreateParcelleActivity;
import com.wedevgroup.weflyhelper.model.Parcelle;
import com.wedevgroup.weflyhelper.model.Point;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 11/04/2018.
 */

public class DrawingPresenter implements Serializable{
    public GoogleMapCustom map = null;
    Polygon polygon;
    Polyline polyline;
    PolylineOptions polylineOptions;
    private final String TAG = getClass().getSimpleName();
    private final CreateParcelleActivity act;
    private View v;
    OnDrawingComleteListener listener;
    private int idOnMap;
    private PointCustom pCenter = null, pRef = null,pRefCopy = null, origin = null;
    boolean isFromEditMode;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<PointCustom>> drawingList = new CopyOnWriteArrayList<>(); // use to allow back btn
    private CopyOnWriteArrayList<PointCustom> pointsParcels = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<CopyOnWriteArrayList<PointCustom>> savDrawing = new CopyOnWriteArrayList<>(); // use to allow back btn
    private static CopyOnWriteArrayList<PointCustom> savPoints = new CopyOnWriteArrayList<PointCustom>();


    public DrawingPresenter(@NonNull final CreateParcelleActivity activity, @NonNull final GoogleMapCustom map, @NonNull View view){
        this.act = activity;
        this.map = map;
        this.v = view;
        idOnMap = 0;
    }

    public void addDrawingOnMap(@NonNull LatLng point, boolean isReference) {
        try {
            PointCustom p = new PointCustom();
            p.getPoint().setLatLng(point);
            p.getPoint().setReference(isReference);
            p.setIdOnMap(idOnMap);


            pointsParcels.add(p);

            if (!isReference){

                // Draw Reference points
                onDrawingStart();

                if(pointsParcels.size() > 2){
                    // CASE Polygon
                    // Sort
                    try{
                        List<PointCustom> listMo = new LinkedList<>(pointsParcels);

                        Collections.sort(listMo, new Comparator<PointCustom>(){
                            public int compare(PointCustom obj1, PointCustom obj2) {
                                // ## Ascending order
                                //return obj1.firstName.compareToIgnoreCase(obj2.firstName); // To compare string values
                                return Integer.valueOf(obj1.getIdOnMap()).compareTo(obj2.getIdOnMap()); // To compare integer values

                                // ## Descending order
                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                            }
                        });
                        pointsParcels.clear();
                        pointsParcels.addAll(listMo);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    act.showCalculationBar();
                    saveCenter();
                }else if (pointsParcels.size() > 0){
                    // CASE Polyline

                    // CASE origin
                    if (pointsParcels.size() > 1){
                        if (origin != null){
                            try{
                                origin.getMarker().remove();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    moveCamera();
                }
                addDrawing(pointsParcels);
            }else {
                // Add ref points
                // Ref
                if (pointsParcels.size() > 2){
                    // A polygon exist
                    try {
                        if (isFromEditMode){
                            if (pRef != null){
                                map.addMarker(new MarkerOptions()
                                                .snippet(pRef.getIdOnMapAsString())
                                                .visible(true)
                                                .draggable(true)
                                                .position(pRef.getPoint().getLatLng())
                                                .icon(Utils.getMarkerIconFromDrawable(ContextCompat.getDrawable(act,R.drawable.ic_map_green)))
                                        , pointsParcels,pRef);

                                // allow marker Drag and Drop


                                // Remove last pointsParcels.add(p);
                                pointsParcels.remove(pointsParcels.get(pointsParcels.size() - 1));
                                // add Reference
                                pointsParcels.add(pRef);
                                map.updatePointsList(pointsParcels);
                                isFromEditMode = false;
                                // made copy
                                pRefCopy = pRef;
                            }else {
                                restoreCopy();
                            }

                        }else{
                            // add ref
                            if (pRef != null){
                                try{
                                    pRef.getMarker().remove();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            // Not From Edit mode + undo
                            if (pRefCopy != null)
                                restoreCopy();
                            else {
                                pRef = map.addMarker(new MarkerOptions()
                                                .snippet(p.getIdOnMapAsString())
                                                .visible(true)
                                                .draggable(true)
                                                .position(p.getPoint().getLatLng())
                                                .icon(Utils.getMarkerIconFromDrawable(ContextCompat.getDrawable(act,R.drawable.ic_map_green))),
                                        pointsParcels,p);
                            }


                        }

                        saveCenter();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            onDrawingEnd(isReference, pointsParcels);
            idOnMap++;

            if (!isReference){
                shouldShowSave();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void restoreCopy() throws Exception {
        if (pRefCopy != null){
            pRef = pRefCopy;
            map.addMarker(new MarkerOptions()
                            .snippet(pRef.getIdOnMapAsString())
                            .visible(true)
                            .draggable(true)
                            .position(pRef.getPoint().getLatLng())
                            .icon(Utils.getMarkerIconFromDrawable(ContextCompat.getDrawable(act,R.drawable.ic_map_green)))
                    , pointsParcels,pRef);

            // allow marker Drag and Drop


            // Remove last pointsParcels.add(p);
            pointsParcels.remove(pointsParcels.get(pointsParcels.size() - 1));
            // add Reference
            pointsParcels.add(pRef);
            map.updatePointsList(pointsParcels);
        }
    }


    public void addDrawingOnMap(@NonNull PointCustom point, boolean isReference,@NonNull CopyOnWriteArrayList<PointCustom> list) throws Exception{
        CopyOnWriteArrayList<PointCustom> pointCustoms = new CopyOnWriteArrayList<>();
        PointCustom p = new PointCustom();
        p.getPoint().setLatLng(point.getPoint().getLatLng());
        p.getPoint().setReference(isReference);
        p.setIdOnMap(point.getIdOnMap());

        pointCustoms.add(p);
        pointCustoms.addAll(list);


        //C CODE
        if (pointCustoms.size() > 2){
            try{
                List<PointCustom> listMo = new LinkedList<>(pointCustoms);

                Collections.sort(listMo, new Comparator<PointCustom>(){
                    public int compare(PointCustom obj1, PointCustom obj2) {
                        // ## Ascending order
                        //return obj1.firstName.compareToIgnoreCase(obj2.firstName); // To compare string values
                        return Integer.valueOf(obj1.getIdOnMap()).compareTo(obj2.getIdOnMap()); // To compare integer values

                        // ## Descending order
                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                    }
                });
                pointCustoms.clear();
                pointCustoms.addAll(listMo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        pointsParcels.clear();
        pointsParcels.addAll(pointCustoms);

        if (!isReference){

            // Draw Reference points
            onDrawingStart();

            if(pointCustoms.size() > 2){
                // CASE Polygon
                act.showCalculationBar();
                saveCenter();
            }else if (pointCustoms.size() > 0){
                // CASE Polyline

                // CASE origin
                if (pointCustoms.size() > 1){
                    if (origin != null){
                        try{
                            origin.getMarker().remove();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                moveCamera();
            }
            addDrawing(pointCustoms);
        }
        onDrawingEnd(isReference, pointCustoms);

        shouldShowSave();


    }


    private void addMarkerRef() throws Exception {
        if (pRef != null){
            try{
                pRef.getMarker().remove();
            }catch (Exception e){
                e.printStackTrace();
            }

            map.addMarker(new MarkerOptions()
                            .snippet(pRef.getIdOnMapAsString())
                    .visible(true)
                    .draggable(true)
                    .position(pRef.getPoint().getLatLng())
                    .icon(Utils.getMarkerIconFromDrawable(ContextCompat.getDrawable(act,R.drawable.ic_map_green))),
                    pointsParcels,pRef);

            removeReference(false, false);
            pointsParcels.add(pRef);

            map.updatePointsList(pointsParcels);
        }

    }

    private void saveCenter() throws Exception {
        // Add polygon pCenter
        if (pCenter == null)
            pCenter = new PointCustom();

        pCenter.getPoint().setCenter(true);
        // Fix nearby bug
        CopyOnWriteArrayList<PointCustom> list = new CopyOnWriteArrayList<>();
        list.addAll(pointsParcels);
        // remove Reference
        for (PointCustom dm: list){
            if (dm.getPoint().isReference()){
                list.remove(dm);
                break;
            }
        }

        pCenter.getPoint().setLatLng(Utils.getCenterOfPolygon(list));
    }

    private void moveCamera() throws Exception {
        if (map != null){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    pointsParcels.get(pointsParcels.size() - 1).getPoint().getLatLng(), Constants.MAP_MIN_ZOOM_PREFERENCE));
        }
    }

    public void onUndo(){
        try {
            // show clean Everything
            if (isFromEditMode ){
                if(drawingList.size() == 1 ){
                    requestClearPermission();
                }else
                    undo();
            }else
                undo();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void undo() throws Exception  {// Reference already exist
        if (pRef != null){
            try {
                pRef.getMarker().remove();
            }catch (Exception e){
                e.printStackTrace();
            }

            cleanCenterAndRef();

        }

        // resetIdon map
        resetIdOnMap();

        onDrawingStart();
        addLastDrawingOnMap();
        onDrawingEnd();
    }

    private void cleanCenterAndRef() {
        pRef = null;
        pCenter = null;

    }

    private void resetIdOnMap() throws Exception {
        idOnMap = 0;
        if (pointsParcels.size()> 0){
            for (PointCustom pm: pointsParcels){
                pm.setIdOnMap(idOnMap);
                idOnMap++;
            }
            map.updatePointsList(pointsParcels);
        }
    }

    public void onClear() throws Exception{

        clearPolygon();
        clearPolyline();
        clearMarker(); // clear map too
        pointsParcels.clear();
        drawingList.clear();
        resetIdOnMap();

        pRef = null;
        pCenter = null;

        act.hideCalcultionBar();

    }

    public void onClearComplete() throws Exception{

        clearPolygon();
        clearPolyline();
        clearMarker(); // clear map too
        pointsParcels.clear();
        drawingList.clear();
        resetIdOnMap();

        pRef = null;
        pCenter = null;

        act.hideCalcultionBar();

        notifyOnDrawingComleteListener(true);

    }


    public void onDrawingStart() throws Exception{
        clearPolygon();
        clearPolyline();
        map.clear();
    }

    private void clearMarker() throws Exception {
        if (origin != null){
            try {
                origin.getMarker().remove();
                origin = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (pRef != null){
            try {
                pRef.getMarker().remove();
                pRef = null;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        map.clear();
    }

    public void onDrawingEnd(boolean isRef, @NonNull CopyOnWriteArrayList<PointCustom> list) throws Exception{
        if (!isRef){
            // Save for undo
            final CopyOnWriteArrayList<PointCustom> newList = new CopyOnWriteArrayList<PointCustom>();
            for (PointCustom pm: list){
                PointCustom point = new PointCustom();
                point.getPoint().setPointId(pm.getPoint().getPointId());
                point.getPoint().setReference(pm.getPoint().isReference());
                point.getPoint().setCenter(pm.getPoint().isCenter());
                point.getPoint().setParcelleId(pm.getPoint().getParcelleId());
                point.getPoint().setLatitude(pm.getPoint().getLatitude());
                point.getPoint().setLongitude(pm.getPoint().getLongitude());
                point.getPoint().setRang(pm.getPoint().getRang());
                point.setIdOnMap(pm.getIdOnMap());
                newList.add(point);
            }
            drawingList.add(newList);
            Utils.showToast(act,R.string.catch_done, v);
        }
        refreshDisplay(list);

    }

    private void refreshDisplay(@NonNull CopyOnWriteArrayList<PointCustom> list) throws Exception {

        boolean isRefFound = false;
        boolean isCenterFound = false;

        if (pCenter != null)
            isCenterFound = true;
        else
            isCenterFound = false;
        if (pRef != null){
            if (isFromEditMode)
                isRefFound = false;
            else
                isRefFound = true;

        }
        else
            isRefFound = false;

        if (list.size() > 2){

            if(isRefFound){
                if (isCenterFound){
                    act.onDisplayPanel(true, false, false);
                }else {
                    act.onDisplayPanel(false, false, false);
                }
            }else{
                act.onDisplayPanel(false, true, true);
            }
        }else
            act.onDisplayPanel(false,false, true);

        //removeReference
        for (PointCustom pDm: list){
            try {

            }catch (Exception e){
                e.printStackTrace();
            }
            if (pDm.getPoint().isReference()){
                list.remove(pDm);
                break;
            }
        }

        //remove Center
        for (PointCustom pDm: list){
            try {

            }catch (Exception e){
                e.printStackTrace();
            }
            if (pDm.getPoint().isCenter()){
                list.remove(pDm);
                break;
            }
        }
        act.displayCalculation(list);
    }

    public void onDrawingEnd() throws Exception {
        refreshDisplay(pointsParcels);
    }

    private void clearPolyline() {
        /*Remove hold polygone*/
        if (polyline != null){
            polyline.remove();
            polylineOptions = null;
        }
    }

    private void  clearPolygon(){
        if (polygon != null) {
            polygon.remove();
            polygon = null;
        }
    }

    public void addDrawing(@NonNull CopyOnWriteArrayList<PointCustom> list) throws Exception {
        if (map != null){
            removeCenter();
            removeReference(false, true);

            if (list.size() == 1) {

                // origin
                try {
                    // Fix crash onMulti touch for funny
                    origin = map.addMarker(new MarkerOptions()
                                    .snippet(list.get(list.size() - 1).getIdOnMapAsString())
                            .position(list.get(list.size() - 1).getPoint().getLatLng())
                            .draggable(true)
                            .visible(true)
                            .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(R.drawable.point_mk, 50, 50, act))),
                            pointsParcels,list.get(list.size() - 1));

                }catch (Exception e){
                    e.printStackTrace();
                }


            } else if(list.size() == 2){
                // add Polyline


                // draw pointsParcels or polylines
                if (polylineOptions == null) {
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(ContextCompat.getColor(act, R.color.colorTransparentAccent)).width(5);
                }
                polylineOptions.addAll(getPointsAsLatLng(list));
                polyline = map.addPolyline(polylineOptions);

                // add Marker
                addMarkerOnMap(list);


            } else {
                // add Polygon
                CopyOnWriteArrayList<LatLng> newList = new CopyOnWriteArrayList<>();
                newList.addAll(getPointsAsLatLng(list));

                // Fix empty list issue
                try {
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.addAll(newList);
                    polygonOptions.strokeColor(act.getResources().getColor(R.color.colorTintAccent)).strokeWidth(5).fillColor(act.getResources().getColor(R.color.colorTransparentAccent));
                    polygon = map.addPolygon(polygonOptions);

                    centerCamera(newList);
                    // add Marker
                    addMarkerOnMap(list);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }

    }

    private void centerCamera(@NonNull CopyOnWriteArrayList<LatLng> newList) throws Exception {

        if (map!= null){
            try {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng dm : newList){
                    builder.include(dm);
                }
                LatLngBounds bounds = builder.build();

                int width = act.getResources().getDisplayMetrics().widthPixels;
                int height = act.getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                map.moveCamera(cu);
            } catch (Exception e ){
                e.printStackTrace();
            }
        }
    }


    private void addMarkerOnMap(@NonNull CopyOnWriteArrayList<PointCustom> list) throws Exception {
        for (PointCustom mDm: list){
            try {
                map.addMarker(new MarkerOptions()
                                .snippet(mDm.getIdOnMapAsString())
                        .position(mDm.getPoint().getLatLng())
                        .draggable(true)
                        .visible(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(R.drawable.point_mk, 50, 50, act))),
                        pointsParcels,mDm);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }



    public void addLastDrawingOnMap() throws Exception {

        if (drawingList.size() > 1){
            // before last
            CopyOnWriteArrayList<PointCustom> last = new CopyOnWriteArrayList<PointCustom>();
            last.addAll(drawingList.get(drawingList.size() - 2));

            // update current drawing
            pointsParcels.clear();
            pointsParcels.addAll(last);
            // display
            addDrawing(pointsParcels);
            // remove last
            if (drawingList.size() > 1){
                drawingList.remove(drawingList.get(drawingList.size() -1 ));
            }
            else
                drawingList.clear();
        }else {
            onClear();
        }

    }


    public CopyOnWriteArrayList<PointCustom> getPointsParcels(){
        try {
            for (int i = 0; i < pointsParcels.size() ; i++ ){
                pointsParcels.get(i).getPoint().setRang(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  pointsParcels;
    }

    public int getDrawingSize(){
        return drawingList.size();
    }

    public int getPointsArraySize(){
        return pointsParcels.size();
    }

    public boolean isParcelle(){
        return pointsParcels.size() > 2;
    }

    public void onMarkerDragStart(@NonNull final PointCustom p){
        try {
            if (p.getPoint().isReference()){
                if (pRef != null)
                    pRef = null;
                pRef = p;
            }

            // remove marker
            for (PointCustom pm: pointsParcels){
                if (p.getIdOnMap() == pm.getIdOnMap())
                    pointsParcels.remove(pm);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public void onMarkerDragEnd(@NonNull final PointCustom marker) {
        try {
            if (marker.getPoint().isReference()){
                addMarkerRef();
            }else{
                removeReference(false, false);
                removeCenter();

                clearMarker();
                this.addDrawingOnMap(marker,false, pointsParcels);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private @NonNull CopyOnWriteArrayList<LatLng> getPointsAsLatLng(@NonNull CopyOnWriteArrayList<PointCustom> list) throws Exception{
        // Convert points for marker funct
        CopyOnWriteArrayList<LatLng> listAsLatLng = new CopyOnWriteArrayList<>();
        for (PointCustom mDm: list){
            listAsLatLng.add(mDm.getPoint().getLatLng());
        }
        return listAsLatLng;
    }

    public @NonNull PointCustom getCenter(){
        if (pCenter == null)
            pCenter = new PointCustom();
        return pCenter;
    }

    public @NonNull PointCustom getReference(){
        if (pRef == null){
            pRef = new PointCustom();
            pRef.getPoint().setReference(true);
        }


        return pRef;
    }

    public void centerCameraOnParcel(){
        try {
            removeReference(false, true);
            removeCenter();
            centerCamera(getPointsAsLatLng(pointsParcels));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void removeCenter() throws Exception {
        for (PointCustom cDm: pointsParcels){
            if (cDm.getPoint().isCenter()){
                pointsParcels.remove(cDm);
                break;
            }
        }
    }

    private void removeReference(boolean isEdMode, boolean withSave ) throws Exception {
        if (isEdMode){
            // remove Reference
            for (PointCustom pDm: pointsParcels){
                if (pDm.getPoint().isReference()){
                    if (withSave)
                        pRef = pDm;
                    pointsParcels.remove(pDm);
                    break;
                }
            }

        }else {
            // remove Reference
            for (PointCustom pDm: pointsParcels){
                if (pDm.getPoint().isReference()){
                    pRef = pDm;
                    pointsParcels.remove(pDm);
                    break;
                }
            }
        }
    }

    public Bundle onSaveInstanceState(@NonNull Bundle outState) {
        try {
            // Fix Marker not serializable
            CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> drawingToSave =  new CopyOnWriteArrayList<>();
            CopyOnWriteArrayList<Point> pointsToSave = new CopyOnWriteArrayList<>();
            for (CopyOnWriteArrayList<PointCustom> oneCustomArray : drawingList){
                CopyOnWriteArrayList<Point> newOneArray = new CopyOnWriteArrayList<>();
                for (PointCustom point: oneCustomArray){
                    newOneArray.add(point.getPoint());
                }
                drawingToSave.add(newOneArray);
            }
            // Extrat point form custom point
            for (PointCustom pm : pointsParcels){
                pointsToSave.add(pm.getPoint());
            }
            // save Drawing
            outState.putSerializable(Constants.PRESENTER_DRAWING, drawingToSave);
            // Save Points
            outState.putSerializable(Constants.PRESENTER_DRAWING_POINTS, pointsToSave);
        }catch (Exception e){
            e.printStackTrace();
        }

        return outState;
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> drawings;
            CopyOnWriteArrayList<CopyOnWriteArrayList<PointCustom>> drawingsCustom = new CopyOnWriteArrayList<>();

            drawings = (CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> ) savedInstanceState.getSerializable(Constants.PRESENTER_DRAWING);
            // Restore correct drawing List
            for (CopyOnWriteArrayList<Point> mPm: drawings){
                CopyOnWriteArrayList<PointCustom> newCustomList = new CopyOnWriteArrayList<>();
                // Extract points
                for (Point point: mPm){
                    PointCustom pc= new PointCustom();
                    pc.setPoint(point);
                    newCustomList.add(pc);
                }
                drawingsCustom.add(newCustomList);
            }

            CopyOnWriteArrayList<Point> points;
            CopyOnWriteArrayList<PointCustom> pointsCustom = new CopyOnWriteArrayList<>();
            points = (CopyOnWriteArrayList<Point>) savedInstanceState.getSerializable(Constants.PRESENTER_DRAWING_POINTS);

            //Convert correct point
            for (Point dm: points){
                PointCustom custom = new PointCustom();
                custom.setPoint(dm);
                pointsCustom.add(custom);
            }


            if (points !=null && drawings != null ){
                savPoints.clear();
                savDrawing.clear();

                // Share drawings between Presenter Instance
                savDrawing.addAll(drawingsCustom);
                savPoints.addAll(pointsCustom);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean isStateAvaiable(){
        return  (savDrawing.size() > 0)  && (savPoints.size() > 0);
    }


    public void onReloadRestoreState() {
        try {
            if (savDrawing.size() > 0 && savPoints.size() > 0){
                onClear();
                pointsParcels.addAll(savPoints);
                drawingList.addAll(savDrawing);
                addDrawing(pointsParcels);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public  void onEditMode(@NonNull Parcelle parcelToUpd) {
        try {
            if (map != null){
                onClear();
                pointsParcels.addAll(parcelToUpd.getPointsCustomList());

                isFromEditMode = true;
                for (PointCustom dm : pointsParcels){
                    dm.setIdOnMap(idOnMap);
                    idOnMap ++;
                }
                // remove Reference
                removeReference(true, true);

                // remove center
                removeCenter();
                addDrawing(pointsParcels);

                // Save for undo
                final CopyOnWriteArrayList<PointCustom> newList = new CopyOnWriteArrayList<PointCustom>();
                for (PointCustom pm: pointsParcels){
                    PointCustom point = new PointCustom();
                    point.getPoint().setPointId(pm.getPoint().getPointId());
                    point.getPoint().setReference(pm.getPoint().isReference());
                    point.getPoint().setCenter(pm.getPoint().isCenter());
                    point.getPoint().setParcelleId(pm.getPoint().getParcelleId());
                    point.getPoint().setLatitude(pm.getPoint().getLatitude());
                    point.getPoint().setLongitude(pm.getPoint().getLongitude());
                    point.getPoint().setRang(pm.getPoint().getRang());
                    point.setIdOnMap(pm.getIdOnMap());
                    newList.add(point);
                }
                drawingList.add(newList);

                onDrawingEnd();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void requestClearPermission(){
        if (act != null){
            if (drawingList.size() > 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle(act.getString(R.string.delete_title));
                builder.setMessage(act.getString(R.string.msg_delete));
                builder.setPositiveButton(act.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // remove all points
                                try {
                                    onClearComplete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                builder.setNegativeButton(act.getString(R.string.cancel_dialog),
                        null);
                builder.show();
            }

        }

    }

    public static interface OnDrawingComleteListener {
        void onClearSucces();
        void onUndo();
    }

    public void setOnDrawingComleteListener(@NonNull OnDrawingComleteListener listener) {
        this.listener = listener;
    }

    private void notifyOnDrawingComleteListener(boolean isClear) {
        if (listener != null  && v != null){
            try{
                if (isClear)
                    listener.onClearSucces();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private void shouldShowSave() throws Exception{
        //Delete Reference Point
        if (pRef != null){
            try {
                pRef.getMarker().remove();
            }catch (Exception e){
                e.printStackTrace();
            }
            removeReference(false,false);


            // Show Panel without Save
            onDrawingEnd();
        }




    }
}

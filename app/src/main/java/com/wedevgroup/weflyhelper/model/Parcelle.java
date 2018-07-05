package com.wedevgroup.weflyhelper.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.PointCustom;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.utils.CacheImage;
import com.wedevgroup.weflyhelper.utils.Constants;
import com.wedevgroup.weflyhelper.utils.Utils;
import com.wedevgroup.weflyhelper.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 27/03/2018.
 */

public class Parcelle implements Serializable {
    private static final long serialVersionUID = 10L;
    private int parcelleId;
    private int regionId;
    private double perimetre;
    private double surface;
    private String dateSoumission;
    private String dateCreated;
    private String nomGuide;
    private String emailGuide;
    private String telGuide;
    private String couche;
    private String region;
    private String zone;
    private String distance;
    private long duration;
    private int entrepriseId;
    private boolean isDelete;
    private boolean isNew;
    private int idOnServer;
    private int coucheId;
    private ArrayList<Point> pointsList = new ArrayList<Point>();
    private ArrayList<Culture> cultureList = new ArrayList<Culture>();
    private final String TAG = getClass().getSimpleName();


    public Parcelle(int id, String nomGuide, String emailGuide, String telGuide, double perimetre, double surface, String dateCreated, String dateSoumission, int entrepriseId, String couche, String region, String zone, @NonNull ArrayList<Point> list, @NonNull ArrayList<Culture> cultureList){

        this.parcelleId = id;
        this.perimetre = perimetre;
        this.surface = surface;
        this.dateSoumission = dateSoumission;
        this.dateCreated = dateCreated;
        this.nomGuide = nomGuide;
        this.emailGuide = emailGuide;
        this.telGuide = telGuide;
        this.couche = couche;
        this.region = region;
        this.zone = zone;
        this.entrepriseId = entrepriseId;
        this.pointsList.addAll(list);
        this.cultureList.addAll(cultureList);
    }

    public Parcelle(){

    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }


    public double getPerimetre() {
        return perimetre;
    }

    public void setPerimetre(double perimetre) {
        this.perimetre = perimetre;
    }

    public double getSurface() {
        return surface;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }


    public String getDateSoumission() {
        if(dateSoumission == null)
            dateSoumission = "";
        return dateSoumission;
    }

    public void setDateSoumission(String dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public String getDateCreated() {
        if(dateCreated == null)
            dateCreated = "";
        return dateCreated;
    }

    public @NonNull String getDateCreatedFormatted(){
        String date = "";

        try {
            Date pDate = Utils.getDateFromStringCopy(getDateCreated());
            date = Utils.convertDate(pDate.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;

    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public @NonNull ArrayList<Point> getPointsList() {
        for (Point dm : pointsList){
            dm.setParcelleId(getParcelleId());
        }
        return pointsList;
    }

    public @NonNull ArrayList<PointCustom> getPointsCustomList() {
        ArrayList<PointCustom> list = new ArrayList<PointCustom>();
        if (getPointsList().size() > 0){
            for (Point pt: getPointsList()){
                PointCustom custom = new PointCustom();
                custom.setPoint(pt);
                list.add(custom);
            }
        }
        return list;
    }

    public String getNameGuide() {
        if (nomGuide == null)
            nomGuide = "";
        return nomGuide;
    }

    public void setNameGuide(String nomGuide) {
        this.nomGuide = nomGuide;
    }

    public String getEmailGuide() {
        if (emailGuide == null)
            emailGuide = "";
        return emailGuide;
    }

    public void setEmailGuide(String emailGuide) {
        this.emailGuide = emailGuide;
    }

    public String getTelGuide() {
        if (telGuide == null)
            telGuide = "";
        return telGuide;
    }

    public void setTelGuide(String telGuide) {
        this.telGuide = telGuide;
    }

    public void setPointsList(@NonNull ArrayList<Point> pointsList) {
        this.pointsList.clear();
        this.pointsList.addAll(pointsList);
    }

    public int getParcelleId() {
        return parcelleId;
    }

    public void setParcelleId(int parcelleId) {
        this.parcelleId = parcelleId;
    }


    public String getCouche() {
        if (couche == null)
            couche = "";
        return couche;
    }

    public void setCouche(String couche) {
        this.couche = couche;
    }

    public String getRegion() {
        if (region == null)
            region = "";
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getZone() {
        if (zone ==  null)
            zone = "";
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }


    public int getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(int entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public ArrayList<Culture> getCultureList() {
        return cultureList;
    }

    public void setCultureList(@NonNull ArrayList<Culture> cultureList) {
        this.cultureList.clear();
        this.cultureList.addAll(cultureList);
    }

    public @NonNull String parcelleToStringWithServId(int index, @NonNull final Context ctx){
        if (ctx != null){
            if (getPointsList().size() >  0 && getCultureList().size() > 0){
                ArrayList<Point> pointList = new ArrayList<Point>();
                ArrayList<Culture> cultures = new ArrayList<Culture>();
                pointList.addAll(getPointsList());
                cultures.addAll(getCultureList());


                String parcelStr =

                        "%polygone"+ index +"%"+":" +
                                "[ " + "%{°poly°"+ " : "     +

                                "{" +   "°perimetre°"+      ":"  +  "°"+ getPerimetre()         + "°"+ ","+
                                "°surface°"+                ":"  +  "°"+ getSurface()           + "°"+ ","+
                                "°dateCreated°"+            ":"  +  "°"+ getDateCreated()       + "°"+ ","+
                                "°nomGuide°"+               ":"  +  "°"+ getNameGuide()          + "°"+ ","+
                                "°mid°"+                    ":"  +  "°"+ getParcelleId()        + "°"+ ","+
                                "°id°"+                     ":"  +  "°"+ getIdOnServer()        + "°"+ ","+
                                "°emailGuide°"+             ":"  +  "°"+ getEmailGuide()        + "°"+ ","+
                                "°telGuide°"+               ":"  +  "°"+ getTelGuide()          + "°"+ ","+
                                "°couche°"+                 ":"  +  "°"+ getCoucheId()            + "°"+ ","+
                                "°region°"+                 ":"  +  "°"+ getRegionId()                    + "°"+ ","+
                                "°isDelete°"+               ":"  +  "°"+ getIsDeleteAsString()            + "°"+ ","+ "".replace('t','T' ).replace('f','F' )        +
                                "°distance°"+               ":"  +  "°"+ getDistance()                    + "°"+ ","+
                                "°duree°"+                  ":"  +  "°"+ getDuration()                    + "°"+ ","+
                                "°zone°"+                   ":"  +  "°"+ getZone()                        + "°"+ ","+
                                "°photo°"+                  ":"  +  "°"+ Constants.ENCODE_IMAGE_VALUE     + "°"+ ","+
                                "°entrepriseId°"+           ":"  +  "°"+  getEntrepriseId()     + "°" +"}"+ "," +
                                "°points°"+ ": [";
                String pointFormated = "";

                if (getPointsList().size() > 1){
                    for (int i = 0; i <= (pointList.size() -2); i++){
                        pointFormated += "{"+
                                "°la&i&ude°"+                ":"  + "°"+ pointList.get(i).getLatitude()        + "°"+ ","+
                                "°longi&ude°"+               ":"  + "°"+ pointList.get(i).getLongitude()       + "°"+ ","+
                                "°rang°"+                    ":"  + "°"+ pointList.get(i).getRang()            + "°"+ ","+
                                "°isRe;erence°"+             ":"  + "°"+ pointList.get(i).isReference()        + "°"+ ","+
                                "°isCen&er°"+                ":"  + "°"+ pointList.get(i).isCenter()           + "°"+ "}, ";
                    }
                }

                // for last point
                pointFormated += "{"+
                        "°la&i&ude°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).getLatitude()        + "°"+ ","+
                        "°longi&ude°"+               ":"  + "°"+ pointList.get(pointList.size() -1 ).getLongitude()       + "°"+ ","+
                        "°rang°"+                    ":"  + "°"+ pointList.get(pointList.size() -1 ).getRang()            + "°"+ ","+
                        "°isRe;erence°"+             ":"  + "°"+ pointList.get(pointList.size() -1 ).isReference()        + "°"+ ","+
                        "°isCen&er°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).isCenter()           + "°"+ "} ]" + "," ;

                // convert  true TO True and false to False
                pointFormated = pointFormated.replace('t','T' );
                pointFormated = pointFormated.replace('f','F' );
                pointFormated = pointFormated.replaceAll("&","t" );
                pointFormated = pointFormated.replaceAll(";","f" );

                parcelStr += pointFormated;




                // add cultures
                parcelStr += "°culture°"+ ": [";

                if (cultures.size() > 1){
                    for (int i = 0; i <= (cultures.size() -2); i++){
                        parcelStr += "{"+
                                "°id°"+                     ":"  +  "°"+ cultures.get(i).getCultureId()                             + "°" + "}, ";
                    }
                }

                // for last culture
                parcelStr += "{"+
                        "°id°"+                      ":"  +   "°"+ cultures.get(cultures.size() -1 ).getCultureId()          + "°" + "} ]";

                parcelStr +=    "}%"
                        +"]"

                ;// close json object

                parcelStr =         parcelStr.replace('%','"');
                parcelStr =         parcelStr.replaceAll(Constants.ENCODE_IMAGE_VALUE, getImageAsString(ctx));

                return parcelStr.replaceAll("°", "'");
            }
        }

        String s = "";
        return s;


    }

    public @NonNull String parcelleToString(int index, @NonNull final Context ctx){
        if (ctx != null){
            if (getPointsList().size() >  0 && getCultureList().size() > 0){
                ArrayList<Point> pointList = new ArrayList<Point>();
                ArrayList<Culture> cultures = new ArrayList<Culture>();
                pointList.addAll(getPointsList());
                cultures.addAll(getCultureList());


                String parcelStr =

                        "%polygone"+ index +"%"+":" +
                                "[ " + "%{°poly°"+ " : "     +

                                "{" +   "°perimetre°"+      ":"  +  "°"+ getPerimetre()         + "°"+ ","+
                                "°surface°"+                ":"  +  "°"+ getSurface()           + "°"+ ","+
                                "°dateCreated°"+            ":"  +  "°"+ getDateCreated()       + "°"+ ","+
                                "°nomGuide°"+               ":"  +  "°"+ getNameGuide()          + "°"+ ","+
                                "°mid°"+                    ":"  +  "°"+ getParcelleId()        + "°"+ ","+
                                "°emailGuide°"+             ":"  +  "°"+ getEmailGuide()        + "°"+ ","+
                                "°telGuide°"+               ":"  +  "°"+ getTelGuide()          + "°"+ ","+
                                "°couche°"+                 ":"  +  "°"+ getCoucheId()            + "°"+ ","+
                                "°region°"+                 ":"  +  "°"+ getRegionId()                    + "°"+ ","+
                                "°isDelete°"+               ":"  +  "°"+ getIsDeleteAsString()            + "°"+ ","+ "".replace('t','T' ).replace('f','F' )    +
                                "°distance°"+               ":"  +  "°"+ getDistance()                    + "°"+ ","+
                                "°duree°"+                  ":"  +  "°"+ getDuration()                    + "°"+ ","+
                                "°zone°"+                   ":"  +  "°"+ getZone()                        + "°"+ ","+
                                "°photo°"+                  ":"  +  "°"+ Constants.ENCODE_IMAGE_VALUE     + "°"+ ","+
                                "°entrepriseId°"+           ":"  +  "°"+  getEntrepriseId()     + "°" +"}"+ "," +
                                "°points°"+ ": [";
                String pointFormated = "";

                if (getPointsList().size() > 1){
                    for (int i = 0; i <= (pointList.size() -2); i++){
                        pointFormated += "{"+
                                "°la&i&ude°"+                ":"  + "°"+ pointList.get(i).getLatitude()        + "°"+ ","+
                                "°longi&ude°"+               ":"  + "°"+ pointList.get(i).getLongitude()       + "°"+ ","+
                                "°rang°"+                    ":"  + "°"+ pointList.get(i).getRang()            + "°"+ ","+
                                "°isRe;erence°"+             ":"  + "°"+ pointList.get(i).isReference()        + "°"+ ","+
                                "°isCen&er°"+                ":"  + "°"+ pointList.get(i).isCenter()           + "°"+ "}, ";
                    }
                }

                // for last point
                pointFormated += "{"+
                        "°la&i&ude°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).getLatitude()        + "°"+ ","+
                        "°longi&ude°"+               ":"  + "°"+ pointList.get(pointList.size() -1 ).getLongitude()       + "°"+ ","+
                        "°rang°"+                    ":"  + "°"+ pointList.get(pointList.size() -1 ).getRang()            + "°"+ ","+
                        "°isRe;erence°"+             ":"  + "°"+ pointList.get(pointList.size() -1 ).isReference()        + "°"+ ","+
                        "°isCen&er°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).isCenter()           + "°"+ "} ]" + "," ;

                // convert  true TO True and false to False
                pointFormated = pointFormated.replace('t','T' );
                pointFormated = pointFormated.replace('f','F' );
                pointFormated = pointFormated.replaceAll("&","t" );
                pointFormated = pointFormated.replaceAll(";","f" );


                parcelStr += pointFormated;



                // add cultures
                parcelStr += "°culture°"+ ": [";

                if (cultures.size() > 1){
                    for (int i = 0; i <= (cultures.size() -2); i++){
                        parcelStr += "{"+
                                "°id°"+                     ":"  +  "°"+ cultures.get(i).getCultureId()                             + "°" + "}, ";
                    }
                }

                // for last culture
                parcelStr += "{"+
                        "°id°"+                      ":"  +   "°"+ cultures.get(cultures.size() -1 ).getCultureId()          + "°" + "} ]";

                parcelStr +=    "}%"
                        +"]"

                ;// close json object

                parcelStr =         parcelStr.replace('%','"');
                parcelStr =         parcelStr.replaceAll(Constants.ENCODE_IMAGE_VALUE, getImageAsString(ctx));

                return parcelStr.replaceAll("°", "'");
            }
        }
        String s = "";
        return s;

    }

    public @Nullable JSONObject parcelleToJSONObjAsPostItem() throws Exception{

        if (getPointsList().size() >  0 && getCultureList().size() > 0){
            ArrayList<Point> pointList = new ArrayList<Point>();
            ArrayList<Culture> cultures = new ArrayList<Culture>();
            pointList.addAll(getPointsList());
            cultures.addAll(getCultureList());


            String parcelStr =
                    "{" +
                            "%polygone"+"0"+ "%"+ ":" +
                            "[ " + "%{°poly°"+ " : "     +

                            "{" +   "°perimetre°"+              ":"  +  "°"+ getPerimetre()         + "°"+ ","+
                            "°surface°"+                ":"  +  "°"+ getSurface()           + "°"+ ","+
                            "°dateCreated°"+            ":"  +  "°"+ getDateCreated()       + "°"+ ","+
                            "°nomGuide°"+               ":"  +  "°"+ getNameGuide()          + "°"+ ","+
                            "°mid°"+                    ":"  +  "°"+ getParcelleId()        + "°"+ ","+
                            "°emailGuide°"+             ":"  +  "°"+ getEmailGuide()        + "°"+ ","+
                            "°telGuide°"+               ":"  +  "°"+ getTelGuide()          + "°"+ ","+
                            "°couche°"+                 ":"  +  "°"+ getCoucheId()            + "°"+ ","+
                            "°region°"+                 ":"  +  "°"+ getRegionId()                    + "°"+ ","+
                            "°isDelete°"+               ":"  +  "°"+ getIsDeleteAsString()            + "°"+ ","+ "".replace('t','T' ).replace('f','F' )    +
                            "°distance°"+               ":"  +  "°"+ getDistance()                    + "°"+ ","+
                            "°duree°"+                  ":"  +  "°"+ getDuration()                    + "°"+ ","+
                            "°zone°"+                   ":"  +  "°"+ getZone()                        + "°"+ ","+
                            "°photo°"+                  ":"  +  "°"+ Constants.ENCODE_IMAGE_VALUE     + "°"+ ","+
                            "°entrepriseId°"+           ":"  +  "°"+  getEntrepriseId()     + "°" +"}"+ "," +
                            "°points°"+ ": [";
            String pointFormated = "";

            if (getPointsList().size() > 1){
                for (int i = 0; i <= (pointList.size() -2); i++){
                    pointFormated += "{"+
                            "°la&i&ude°"+                ":"  + "°"+ pointList.get(i).getLatitude()        + "°"+ ","+
                            "°longi&ude°"+               ":"  + "°"+ pointList.get(i).getLongitude()       + "°"+ ","+
                            "°rang°"+                    ":"  + "°"+ pointList.get(i).getRang()            + "°"+ ","+
                            "°isRe;erence°"+             ":"  + "°"+ pointList.get(i).isReference()        + "°"+ ","+
                            "°isCen&er°"+                ":"  + "°"+ pointList.get(i).isCenter()           + "°"+ "}, ";
                }
            }

            // for last point
            pointFormated += "{"+
                    "°la&i&ude°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).getLatitude()        + "°"+ ","+
                    "°longi&ude°"+               ":"  + "°"+ pointList.get(pointList.size() -1 ).getLongitude()       + "°"+ ","+
                    "°rang°"+                    ":"  + "°"+ pointList.get(pointList.size() -1 ).getRang()            + "°"+ ","+
                    "°isRe;erence°"+             ":"  + "°"+ pointList.get(pointList.size() -1 ).isReference()        + "°"+ ","+
                    "°isCen&er°"+                ":"  + "°"+ pointList.get(pointList.size() -1 ).isCenter()           + "°"+ "} ]" + "," ;

            // convert  true TO True and false to False
            pointFormated = pointFormated.replace('t','T' );
            pointFormated = pointFormated.replace('f','F' );
            pointFormated = pointFormated.replaceAll("&","t" );
            pointFormated = pointFormated.replaceAll(";","f" );

            parcelStr += pointFormated;




            // add cultures
            parcelStr += "°culture°"+ ": [";

            if (cultures.size() > 1){
                for (int i = 0; i <= (cultures.size() -2); i++){
                    parcelStr += "{"+
                            "°id°"+                     ":"  +  "°"+ cultures.get(i).getCultureId()                             + "°" + "}, ";
                }
            }

            // for last culture
            parcelStr += "{"+
                    "°id°"+                      ":"  +   "°"+ cultures.get(cultures.size() -1 ).getCultureId()          + "°" + "} ]";

            parcelStr +=    "}%"
                    +"]" +

                    "}" ;// close json object

            parcelStr =         parcelStr.replace('%','"');
            parcelStr =         parcelStr.replaceAll(Constants.ENCODE_IMAGE_VALUE, getImageAsString(AppController.getInstance()));
            String strFinal = parcelStr.replaceAll("°", "'");


            try {
                JSONObject resul = new JSONObject(strFinal);
                return  resul;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }else
            return null;

    }



    public @Nullable Bitmap getImageAsBitmap(final Context ctx){
        Bitmap bp = null;
        try {
            CacheImage cImage = new CacheImage(ctx);
            bp = cImage.load(String.valueOf(getParcelleId()));

            return bp;

            //return Utils.getResizedBitmap(bp, 1280, 960);
        }catch (Exception e){
            e.printStackTrace();
            bp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.img_default_parcel);
        }

        return bp;
    }


    public String getDistance() {
        if (distance == null)
            distance = "";
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getIsDeleteAsInt(){
        int bool = (isDelete)? 1 : 0;
        return  bool;
    }

    public void setDelete(int delete) {
        isDelete = delete == 1;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getIsNewAsInt(){
        int bool = (isNew)? 1 : 0;
        return  bool;
    }

    public void setNew(int aNew) {
        isNew = aNew == 1;
    }

    public String getIsDeleteAsString(){
        String res = "";
        if (isDelete)
            res = "True";
        else
            res = "False";
        return res;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public @NonNull String getImageAsString(final Context ctx){
        String resp = "";
        if (ctx != null){
            try {
                resp = Utils.encodeToBase64(getImageAsBitmap(ctx));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return resp;
    }

    public @NonNull String getImagePath(final @NonNull  Context ctx){
        String path = "";

        try {
            CacheImage cImage = new CacheImage(ctx);
            path = cImage.getImagePath(String.valueOf(getParcelleId()));
        }catch (Exception e){
            e.printStackTrace();
        }

        return path;

    }

    public int getCoucheId() {
        return coucheId;
    }

    public void setCoucheId(int coucheId) {
        this.coucheId = coucheId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}

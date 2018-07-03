package com.wedevgroup.weflyhelper.model;

import java.io.Serializable;

/**
 * Created by admin on 22/06/2018.
 */

public class Profile implements Serializable {
    private int idOnServer;
    private String phone;
    private String image;
    private String entreprise;
    private String fonction;
    private String numCnps;
    private boolean actor;

    public int getIdOnServer() {
        return idOnServer;
    }

    public void setIdOnServer(int idOnServer) {
        this.idOnServer = idOnServer;
    }

    public String getPhone() {
        if (phone == null)
            phone = "";
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        if (image == null)
            image = "";
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEntreprise() {
        if (entreprise == null)
            entreprise = "";
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getFonction() {
        if (fonction == null)
            fonction = "";
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getNumCnps() {
        if (numCnps == null)
            numCnps = "";
        return numCnps;
    }

    public void setNumCnps(String numCnps) {
        this.numCnps = numCnps;
    }

    public boolean isActor() {
        return actor;
    }

    public void setActor(boolean actor) {
        this.actor = actor;
    }
}

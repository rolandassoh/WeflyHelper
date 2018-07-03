package com.wedevgroup.weflyhelper.model;

import java.io.Serializable;

/**
 * Created by admin on 22/06/2018.
 */

public class Account implements Serializable {
    private Profile profile;
    private int id;
    private String email;
    private String lastName;
    private String firstName;
    private String password;
    private String couche;
    private String userName;

    public Profile getProfile() {
        if (profile == null)
            profile = new Profile();
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        if (userName == null)
            userName = "";
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        if (email == null)
            email = "";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        if (lastName == null)
            lastName = "";
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        if (firstName == null)
            firstName ="";
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        if (password == null)
            password = "";
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCouche() {
        if (couche == null)
            couche = "";
        return couche;
    }

    public void setCouche(String couche) {
        this.couche = couche;
    }
}

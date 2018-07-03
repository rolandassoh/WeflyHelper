package com.wedevgroup.weflyhelper.model;

import java.io.Serializable;

/**
 * Created by admin on 27/03/2018.
 */

public class CultureType implements Serializable {
    private static final long serialVersionUID = 10L;
    private int typeCultureId;
    private String name;
    private String description;

    public CultureType(int id, String name, String description){
        typeCultureId =  id;
        this.name = name;
        this.description = description;

    }

    public CultureType(){

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getTypeCultureId() {
        return typeCultureId;
    }

    public void setTypeCultureId(int typeCultureId) {
        this.typeCultureId = typeCultureId;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if(description == null)
            description = "";
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package com.wedevgroup.weflyhelper.model;

import java.util.ArrayList;

/**
 * Created by admin on 07/03/2018.
 */

public class MessageEB {

    private String msg;
    private int number;
    private String classSender;


    public String getMsg() {
        if (msg == null)
            msg = "";
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getClassSender() {
        if (classSender == null)
            classSender ="";
        return classSender;
    }

    public void setClassSender(String classSender) {
        this.classSender = classSender;
    }
}

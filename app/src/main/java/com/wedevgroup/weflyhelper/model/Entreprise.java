package com.wedevgroup.weflyhelper.model;

/**
 * Created by admin on 02/04/2018.
 */

public class Entreprise {
    private static final long serialVersionUID = 10L;
    private int entrepriseId ;
    private String name;
    private String email;
    private String tel;
    private String ref;
    private String webSite;
    private String internalNote;
    private String password;

    public Entreprise(int entrepriseId, String name, String email, String tel, String ref, String internalNote) {
        this.entrepriseId = entrepriseId;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.ref = ref;
        this.internalNote = internalNote;
    }

    public Entreprise() {

    }

    public int getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(int entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        if(email == null)
            email = "";
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        if(tel == null)
            tel = "";
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRef() {
        if(ref == null)
            ref = "";
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getInternalNote() {
        if (internalNote ==  null)
            internalNote = "";
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public String getWebSite() {
        if(webSite == null)
            webSite = "";
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPassword() {
        if (password ==  null)
            password = "";
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

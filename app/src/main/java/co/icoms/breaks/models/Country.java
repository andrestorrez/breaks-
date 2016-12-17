package co.icoms.breaks.models;

import java.io.Serializable;

/**
 * Created by escolarea on 12/7/16.
 */

public class Country implements Serializable {
    private String name;
    private String iso;
    private String lang;


    public Country(String name, String iso, String lang) {
        super();
        this.name = name;
        this.iso = iso;
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}

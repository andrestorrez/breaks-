package co.icoms.breaks;

/**
 * Created by escolarea on 12/4/16.
 */

public class CountryObject {
    private String name;
    private String iso;
    private String lang;

    public CountryObject(String name, String iso, String lang) {
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

    @Override
    public String toString(){
        return name+" | "+lang;
    }
}

package co.icoms.breaks.models;

import java.io.Serializable;

/**
 * Created by escolarea on 12/7/16.
 */

public class Team implements Serializable {
    private String name;
    private Country country;

    public Team(String name, Country country) {
        super();
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}

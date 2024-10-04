package org.uclv.models;

public class Tax {
    private String country_code;
    private String central_code;
    private float value;

    public Tax(String country_code, String central_code, float value) {
        this.country_code = country_code;
        this.central_code = central_code;
        this.value = value;
    }

    public String getCountryCode() {
        return country_code;
    }

    public String getCentralCode() {
        return central_code;
    }

    public float getValue() {
        return value;
    }
}

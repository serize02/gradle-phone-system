package org.uclv.models;

import org.uclv.exceptions.WrongCodeFormatE;
import org.uclv.exceptions.WrongTaxCodeE;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

public class Tax implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String country_code;
    private String central_code;
    private float value;

    public Tax(String country_code, String central_code, float value) throws WrongTaxCodeE {
        verifyFormat(country_code);
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

    /**
     * Verifica que el código de país de la tarifa cumpla con el formato establecido
     * @param country_code Código de país de la tarifa
     * @throws WrongTaxCodeE
     */
    private void verifyFormat(String country_code) throws WrongTaxCodeE {
        int index = 1;
        boolean wrong_format = (country_code.charAt(0) != '+') || (country_code.length() != 4);

        while (index < 4 && !wrong_format) {
            if (!Character.isDigit(country_code.charAt(index))) {
                wrong_format = true;
            }
            index++;
        }

        if (wrong_format)
            throw new WrongTaxCodeE();
    }
}

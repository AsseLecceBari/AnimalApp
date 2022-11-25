package model;

import java.io.Serializable;

public class Adozione  implements Serializable {
    private String idAnimale;

    public Adozione(){}

    public Adozione(String idAnimale) {
        this.idAnimale = idAnimale;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public void setIdAnimale(String idAnimale) {
        this.idAnimale = idAnimale;
    }

    public void setEmailProprietario(String emailProprietario) {

    }
}

package model;

import java.io.Serializable;

public class Adozione  implements Serializable {
    private String idAnimale;
    private String idAdozione;
    private String emailProprietario;
    private String dataPubblicazione;
    private String descrizione;

    public Adozione(){}

    public Adozione(String idAnimale, String idAdozione, String emailProprietario, String dataPubblicazione,String descrizione) {
        this.idAnimale = idAnimale;
        this.idAdozione= idAdozione;
        this.emailProprietario=emailProprietario;
        this.dataPubblicazione=dataPubblicazione;
        this.descrizione= descrizione;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getIdAdozione() {
        return idAdozione;
    }

    public String getEmailProprietario() {
        return emailProprietario;
    }

    public String getDataPubblicazione() {
        return dataPubblicazione;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String s) {
         descrizione= s;
    }


}

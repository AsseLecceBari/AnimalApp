package model;

import java.io.Serializable;
import java.util.Map;

public class Segnalazione implements Serializable {
    String tipo;
    String idAnimale;
    String idSegnalazione;
    String descrizione;
    Map<String,String> coordinateGps;



    public Segnalazione(String idSegnalazione, String tipo, String idAnimale, String descrizione, Map<String, String> coordinateGps) {
        this.tipo = tipo;
        this.idAnimale = idAnimale;
        this.idSegnalazione = idSegnalazione;
        this.descrizione = descrizione;
        this.coordinateGps = coordinateGps;
    }
    public Segnalazione() {

    }
    public Segnalazione( String descrizione) {

        this.descrizione = descrizione;

    }



    public String getTipo() {
        return tipo;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Map<String, String> getCoordinateGps() {
        return coordinateGps;
    }

    public String getIdSegnalazione() {
        return idSegnalazione;
    }

}

package model;

import java.util.Map;

public class Segnalazione {
    int tipo;
    int idAnimale;
    String descrizione;
    Map<String,String> coordinateGps;

    public Segnalazione(int tipo, int idAnimale, String descrizione, Map<String, String> coordinateGps) {
        this.tipo = tipo;
        this.idAnimale = idAnimale;
        this.descrizione = descrizione;
        this.coordinateGps = coordinateGps;
    }
    public Segnalazione() {

    }
    public Segnalazione( String descrizione) {

        this.descrizione = descrizione;

    }



    public int getTipo() {
        return tipo;
    }

    public int getIdAnimale() {
        return idAnimale;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Map<String, String> getCoordinateGps() {
        return coordinateGps;
    }
}

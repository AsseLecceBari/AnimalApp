package model;

import java.io.Serializable;
import java.util.Map;

public class Segnalazione implements Serializable {

    /**
     * 1-smarrimento
     * 2-animale in pericolo
     * 3-zona pericolosa/animale pericoloso
     * 4-news positive
     * 5-raccolta fondi
     * 6-ritrovamento
     */
    
    private String tipo;
    private String idAnimale;
    private String idSegnalazione;
    private String descrizione;
    private String coordinateGps;
    private String data, urlFoto, link;

    public Segnalazione(String tipo, String idAnimale, String idSegnalazione, String descrizione, String coordinateGps, String data, String urlFoto, String link) {
        this.tipo = tipo;
        this.idAnimale = idAnimale;//solo a smarrimento
        this.idSegnalazione = idSegnalazione;
        this.descrizione = descrizione;
        this.coordinateGps = coordinateGps;
        this.data = data;//la prende dal timestamp
        this.urlFoto = urlFoto;
        this.link = link;
    }

    public Segnalazione() {
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

    public String getCoordinateGps() {
        return coordinateGps;
    }

    public String getIdSegnalazione() {
        return idSegnalazione;
    }

    public String getData() {
        return data;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public String getLink() {
        return link;
    }
}

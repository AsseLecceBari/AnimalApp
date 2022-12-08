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
    private String emailSegnalatore;
    private String tipo;
    private String idAnimale;
    private String idSegnalazione;
    private String descrizione;
    private String titolo;//da aggiungere
    private double latitudine,longitudine;
    private String data, urlFoto, link;

    public Segnalazione(String emailSegnalatore,String titolo,String tipo, String idAnimale, String idSegnalazione, String descrizione, double latitudine,double longitudine, String data, String urlFoto, String link) {
        this.emailSegnalatore=emailSegnalatore;
        this.titolo=titolo;
        this.tipo = tipo;
        this.idAnimale = idAnimale;//solo a smarrimento
        this.idSegnalazione = idSegnalazione;
        this.descrizione = descrizione;
        this.latitudine=latitudine;
        this.longitudine=longitudine;
        this.data = data;//la prende dal timestamp
        this.urlFoto = urlFoto;
        this.link = link;
    }
//provvisorio
    public Segnalazione(String emailSegnalatore,String tipo, String idAnimale, String idSegnalazione, String descrizione, double latitudine,double longitudine, String data, String urlFoto, String link) {
        this.emailSegnalatore=emailSegnalatore;

        this.tipo = tipo;
        this.idAnimale = idAnimale;//solo a smarrimento
        this.idSegnalazione = idSegnalazione;
        this.descrizione = descrizione;
        this.latitudine=latitudine;
        this.longitudine=longitudine;
        this.data = data;//la prende dal timestamp
        this.urlFoto = urlFoto;
        this.link = link;
    }

    public Segnalazione(String emailSegnalatore,String titolo,String tipo, String idSegnalazione, String descrizione, double latitudine,double longitudine, String data, String urlFoto) {
        this.emailSegnalatore=emailSegnalatore;
        this.titolo=titolo;
        this.tipo = tipo;
        this.idSegnalazione = idSegnalazione;
        this.descrizione = descrizione;
        this.latitudine=latitudine;
        this.longitudine=longitudine;
        this.data = data;
        this.urlFoto = urlFoto;
    }

    public Segnalazione() {
    }

    public String getTitolo() {
        return titolo;
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

    public double getLatitudine() {
        return latitudine;
    }

    public String getEmailSegnalatore() {
        return emailSegnalatore;
    }

    public double getLongitudine() {
        return longitudine;
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

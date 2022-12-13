package model;

import java.util.Map;

public class Utente {
    private String email; //Stesso valore del documento
    private String telefono;
    private double latitudine, longitudine;
    private String ruolo;
    private String indirizzo;

    public Utente(String email, String telefono, double latitudine, double longitudine, String ruolo, String indirizzo) {
        this.email = email;
        this.telefono = telefono;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.ruolo = ruolo;
        this.indirizzo = indirizzo;
    }

    public Utente() {
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getRuolo() {
        return ruolo;
    }


    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public String getIndirizzo() {
        return indirizzo;
    }
}

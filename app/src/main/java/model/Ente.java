package model;

import java.util.Map;

public class Ente extends Utente{
    private String partitaIva, denominazione;
    private boolean isPrivato;

    public Ente(String email, String telefono, double latitudine, double longitudine, String ruolo, String indirizzo, String partitaIva, String denominazione, boolean isPrivato) {
        super(email, telefono, latitudine, longitudine, ruolo, indirizzo);
        this.partitaIva = partitaIva;
        this.denominazione = denominazione;
        this.isPrivato = isPrivato;
    }

    public Ente(String partitaIva, String denominazione, boolean isPrivato) {
        this.partitaIva = partitaIva;
        this.denominazione = denominazione;
        this.isPrivato = isPrivato;
    }

    public Ente() {

    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public boolean isPrivato() {
        return isPrivato;
    }
}

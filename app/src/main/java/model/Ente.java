package model;

import java.util.Map;

public class Ente extends Utente{
    private String partitaIva,Denominazione;
    private boolean isPrivato;

    public Ente(String email, String telefono, Map<String, String> indirizzo, String ruolo, String partitaIva, String denominazione, boolean isPrivato) {
        super(email, telefono, indirizzo, ruolo);
        this.partitaIva = partitaIva;
        Denominazione = denominazione;
        this.isPrivato = isPrivato;
    }

    public Ente() {

    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public String getDenominazione() {
        return Denominazione;
    }

    public boolean isPrivato() {
        return isPrivato;
    }
}

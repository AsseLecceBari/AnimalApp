package model;

import java.util.Map;

public class Associazione extends Utente{
    private String codiceFiscaleAssociazione, denominazione;

    public Associazione(String email, String telefono, double latitudine, double longitudine, String ruolo, String indirizzo, String codiceFiscaleAssociazione, String denominazione) {
        super(email, telefono, latitudine, longitudine, ruolo, indirizzo);
        this.codiceFiscaleAssociazione = codiceFiscaleAssociazione;
        this.denominazione = denominazione;
    }

    public Associazione(String codiceFiscaleAssociazione, String denominazione) {
        this.codiceFiscaleAssociazione = codiceFiscaleAssociazione;
        this.denominazione = denominazione;
    }

    public Associazione() {
    }

    public String getCodiceFiscaleAssociazione() {
        return codiceFiscaleAssociazione;
    }

    public String getDenominazione() {
        return denominazione;
    }
}

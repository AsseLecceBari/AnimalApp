package model;

import java.util.Map;

public class Associazione extends Utente{
    String codiceFiscaleAssociazione,denominazione;

    public Associazione(String email, String telefono, Map<String, String> indirizzo, String ruolo, String codiceFiscaleAssociazione, String denominazione) {
        super(email, telefono, indirizzo, ruolo);
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

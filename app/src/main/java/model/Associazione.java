package model;

import java.util.Map;

public class Associazione extends Utente{
    private String codiceFiscaleAssociazione, denominazione;

    public Associazione(String email, String telefono,  String ruolo, Map<String,String> indirizzo, String codiceFiscaleAssociazione, String denominazione) {
        super(email, telefono,  ruolo, indirizzo);
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

package model;

import java.util.Map;

public class Utente {
    private String email; //Stesso valore del documento
    private  String telefono;
    private Map<String,String> indirizzo;
    private String ruolo;


    public Utente(String email, String telefono, Map<String, String> indirizzo, String ruolo) {
        this.email = email;
        this.telefono = telefono;
        this.indirizzo = indirizzo;
        this.ruolo = ruolo;

    }

    public Utente() {
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Map<String, String> getIndirizzo() {
        return indirizzo;
    }

    public String getRuolo() {
        return ruolo;
    }


}

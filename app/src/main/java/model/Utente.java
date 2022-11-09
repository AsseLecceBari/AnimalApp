package model;

import java.util.Map;

public class Utente {
    String email; //Stesso valore del documento
    String telefono;
    Map<String,String> indirizzo;
    int ruolo;


    public Utente(String email, String telefono, Map<String, String> indirizzo, int ruolo) {
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

    public int getRuolo() {
        return ruolo;
    }

}

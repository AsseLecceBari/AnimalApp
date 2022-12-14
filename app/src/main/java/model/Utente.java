package model;

import java.io.Serializable;
import java.util.Map;

public class Utente implements Serializable {
    private String email; //Stesso valore del documento
    private String telefono;

    private String ruolo;
    private Map<String,String> indirizzo;

    public Utente(String email, String telefono, String ruolo, Map<String,String> indirizzo) {
        this.email = email;
        this.telefono = telefono;

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




    public Map<String, String> getIndirizzo() {
        return indirizzo;
    }
}

package model;

import java.util.Date;
import java.util.Map;

public class Persona extends Utente{
    String nome,cognome;
    Date dataDiNascita;

    public Persona(String email, String telefono, Map<String, String> indirizzo, String ruolo, String nome, String cognome, Date dataDiNascita) {
        super(email, telefono, indirizzo, ruolo);
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
    }


    public Persona(){

    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public Date getDataDiNascita() {
        return dataDiNascita;
    }
}

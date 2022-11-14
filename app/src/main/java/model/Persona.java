package model;

import java.util.Date;
import java.util.Map;

public class Persona extends Utente{
    String nome,cognome;
    String dataDiNascita;

    public Persona(String email, String telefono, Map<String, String> indirizzo, String ruolo, String nome, String cognome, String dataDiNascita) {
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

    public String getDataDiNascita() {
        return dataDiNascita;
    }
}

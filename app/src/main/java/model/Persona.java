package model;

import java.util.Date;
import java.util.Map;

public class Persona extends Utente{
    private String nome,cognome;
    private String dataDiNascita;

    public Persona(String email, String telefono, double latitudine, double longitudine, String ruolo, String indirizzo, String nome, String cognome, String dataDiNascita) {
        super(email, telefono, latitudine, longitudine, ruolo, indirizzo);
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
    }

    public Persona(String nome, String cognome, String dataDiNascita) {
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

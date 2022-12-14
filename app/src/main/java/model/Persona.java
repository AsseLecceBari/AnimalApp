package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Persona extends Utente implements Serializable {
    private String nome,cognome;
    private String dataDiNascita;

    public Persona(String email, String telefono,  String ruolo, Map<String,String> indirizzo, String nome, String cognome, String dataDiNascita) {
        super(email, telefono, ruolo, indirizzo);
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

package model;

import java.util.Date;
import java.util.Map;

public class Veterinario extends Persona{
    private String numEFNOVI,partitaIva;

    public Veterinario(String email, String telefono, double latitudine, double longitudine, String ruolo, String indirizzo, String nome, String cognome, String dataDiNascita, String numEFNOVI, String partitaIva) {
        super(email, telefono, latitudine, longitudine, ruolo, indirizzo, nome, cognome, dataDiNascita);
        this.numEFNOVI = numEFNOVI;
        this.partitaIva = partitaIva;
    }

    public Veterinario(String nome, String cognome, String dataDiNascita, String numEFNOVI, String partitaIva) {
        super(nome, cognome, dataDiNascita);
        this.numEFNOVI = numEFNOVI;
        this.partitaIva = partitaIva;
    }

    public Veterinario(String numEFNOVI, String partitaIva) {
        this.numEFNOVI = numEFNOVI;
        this.partitaIva = partitaIva;
    }

    public Veterinario(){

    }

    public String getNumEFNOVI() {
        return numEFNOVI;
    }

    public String getPartitaIva() {
        return partitaIva;
    }
}

package model;

import java.util.Date;
import java.util.Map;

public class Veterinario extends Persona{
    private String numEFNOVI,partitaIva;

    public Veterinario(String email, String telefono, double latitudine, double longitudine, String ruolo, String nome, String cognome, String dataDiNascita, String numEFNOVI, String partitaIva) {
        super(email, telefono, latitudine, longitudine, ruolo, nome, cognome, dataDiNascita);
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

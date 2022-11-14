package model;

import java.util.Date;
import java.util.Map;

public class Veterinario extends Persona{
    String numEFNOVI,partitaIva;

    public Veterinario(String email, String telefono, Map<String, String> indirizzo, String ruolo, String nome, String cognome, String dataDiNascita, String numEFNOVI, String partitaIva) {
        super(email, telefono, indirizzo, ruolo, nome, cognome, dataDiNascita);
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

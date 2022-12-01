package model;

import java.util.ArrayList;

public class Preferenze {
  private String emailUtente;
  private ArrayList<String> adozioni;
  private ArrayList<String> segnalazioni;

    public String getEmailUtente() {
        return emailUtente;
    }

    public ArrayList<String> getAdozioni() {
        return adozioni;
    }

    public ArrayList<String> getSegnalazioni() {
        return segnalazioni;
    }

    public void setEmailUtente(String emailUtente) {
        this.emailUtente = emailUtente;
    }

    public void setAdozioni(ArrayList<String> adozioni) {
        this.adozioni = adozioni;
    }

    public void setSegnalazioni(ArrayList<String> segnalazioni) {
        this.segnalazioni = segnalazioni;
    }

    public Preferenze(String emailUtente, ArrayList<String> adozioni, ArrayList<String> segnalazioni)
    {
        this.emailUtente=emailUtente;
        this.adozioni=adozioni;
        this.segnalazioni=segnalazioni;
    }

    public Preferenze()
    {

    }

    public void aggiungiAdozione(String s)
    {
        this.adozioni.add(s);
    }
}

package model;

import android.graphics.Bitmap;

import java.util.Date;

public class Animale {
    String nome,genere,specie,emailProprietario;
    Date dataDiNascita;
    Bitmap fotoProfilo;  //Verificare che si usi Bitmap
    int idAnimale; //Stesso id del documento
    boolean isAssistito;

    public String getNome() {
        return nome;
    }

    public String getGenere() {
        return genere;
    }

    public String getSpecie() {
        return specie;
    }

    public String getEmailProprietario() {
        return emailProprietario;
    }

    public Date getDataDiNascita() {
        return dataDiNascita;
    }

    public Bitmap getFotoProfilo() {
        return fotoProfilo;
    }

    public int getIdAnimale() {
        return idAnimale;
    }

    public boolean isAssistito() {
        return isAssistito;
    }


    public Animale(String nome, String genere, String specie, String emailProprietario, Date dataDiNascita, Bitmap fotoProfilo, int idAnimale, boolean isAssistito) {
        this.nome = nome;
        this.genere = genere;
        this.specie = specie;
        this.emailProprietario = emailProprietario;
        this.dataDiNascita = dataDiNascita;
        this.fotoProfilo = fotoProfilo;
        this.idAnimale = idAnimale;
        this.isAssistito = isAssistito;
    }
    public Animale(){}






}

package model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Animale implements Serializable{
    String nome,genere,specie,emailProprietario;
    String dataDiNascita;
    String fotoProfilo;  //Verificare che si usi Bitmap
    String idAnimale; //Stesso id del documento
    Boolean isAssistito;


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

    public String getDataDiNascita() {
        return dataDiNascita;
    }

    public String getFotoProfilo() {
        return fotoProfilo;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public Boolean getIsAssistito() {
        return isAssistito;
    }




    public Animale(String nome, String genere, String specie, String emailProprietario, String dataDiNascita, String fotoProfilo, String idAnimale, Boolean isAssistito) {
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

    public void setFotoProfilo(String fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }
}

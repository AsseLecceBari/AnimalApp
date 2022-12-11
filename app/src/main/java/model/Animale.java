package model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Animale implements Serializable{
    private String nome,genere,specie,emailProprietario;
    private String dataDiNascita;
    private String fotoProfilo;  //Verificare che si usi Bitmap
    private String idAnimale; //Stesso id del documento
    private Boolean isAssistito;



    private String sesso;
    public String getSesso() {
        return sesso;
    }

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

    public Animale(String nome, String genere, String specie, String emailProprietario, String dataDiNascita, String fotoProfilo, String idAnimale, Boolean isAssistito,String sesso) {
        this.nome = nome;
        this.genere = genere;
        this.specie = specie;
        this.emailProprietario = emailProprietario;
        this.dataDiNascita = dataDiNascita;
        this.fotoProfilo = fotoProfilo;
        this.idAnimale = idAnimale;
        this.isAssistito = isAssistito;
        this.sesso=sesso;
    }

    public Animale(){}

    public void setFotoProfilo(String fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }
}

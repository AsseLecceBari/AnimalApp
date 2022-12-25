package model;

import java.io.Serializable;

public class Animale implements Serializable{
    private String nome,genere,specie,emailProprietario;
    private String dataDiNascita;
    private String fotoProfilo;  //Verificare che si usi Bitmap
    private String idAnimale; //Stesso id del documento
    private Boolean isAssistito;
    private String sesso;
    private String box;
    private String dataRitrovamento;

    public void setMicroChip(String microChip) {
        this.microChip = microChip;
    }

    private String microChip;

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

    public Animale(String nome, String genere, String specie, String emailProprietario, String dataDiNascita, String fotoProfilo, String idAnimale, Boolean isAssistito, String sesso, String box, String dataRitrovamento, String microChip) {
        this.nome = nome;
        this.genere = genere;
        this.specie = specie;
        this.emailProprietario = emailProprietario;
        this.dataDiNascita = dataDiNascita;
        this.fotoProfilo = fotoProfilo;
        this.idAnimale = idAnimale;
        this.isAssistito = isAssistito;
        this.sesso = sesso;
        this.box = box;
        this.dataRitrovamento = dataRitrovamento;
        this.microChip = microChip;
    }

    public Animale(){}

    public void setFotoProfilo(String fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }

    @Override
    public String toString() {
        return "Ecco le info del animale: \n" +
                "nome='" + nome + '\'' +
                ", genere='" + genere + '\'' +
                ", specie='" + specie + '\'' +
                ", emailProprietario='" + emailProprietario + '\'' +
                ", dataDiNascita='" + dataDiNascita + '\'' +
                ", fotoProfilo='" + fotoProfilo + '\'' +
                ", idAnimale='" + idAnimale + '\'' +
                ", isAssistito=" + isAssistito +
                ", sesso='" + sesso + '\'';
    }

    public String getBox() {
        return box;
    }

    public String getDataRitrovamento() {
        return dataRitrovamento;
    }

    public String getMicroChip() {
        return microChip;
    }
}

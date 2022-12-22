package model;

public class Pokedex {
    private String idAnimale;
    private String emailProprietarioPokedex;
    public Pokedex() {
    }
    public Pokedex(String idAnimale, String emailProprietarioPokedex) {
        this.idAnimale = idAnimale;
        this.emailProprietarioPokedex = emailProprietarioPokedex;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getEmailProprietarioPokedex() {
        return emailProprietarioPokedex;
    }


}

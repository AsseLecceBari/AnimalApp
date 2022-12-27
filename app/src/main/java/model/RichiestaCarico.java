package model;

public class RichiestaCarico {
    private String idAnimale;

    public String getStato() {
        return stato;
    }

    private String stato;
    public RichiestaCarico(){}

    public RichiestaCarico(String idAnimale, String idVeterinario, String idProprietario,String stato) {
        this.idAnimale = idAnimale;
        this.idVeterinario = idVeterinario;
        this.idProprietario = idProprietario;
        this.stato=stato;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getIdVeterinario() {
        return idVeterinario;
    }

    public String getIdProprietario() {
        return idProprietario;
    }

    private String idVeterinario;
    private String idProprietario;
}

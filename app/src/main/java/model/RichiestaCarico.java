package model;

public class RichiestaCarico {
    private String idAnimale;
public RichiestaCarico(){}
    public RichiestaCarico(String idAnimale, String idVeterinario, String idProprietario) {
        this.idAnimale = idAnimale;
        this.idVeterinario = idVeterinario;
        this.idProprietario = idProprietario;
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

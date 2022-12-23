package model;

public class SpesaAnimale {
    private String categoria, data, descrizione, id, idAnimale;
    private int quantita;
    private float costoUnitario;

    public SpesaAnimale() {
    }
    public SpesaAnimale(String categoria, String data, String descrizione, String id, String idAnimale, float costoUnitario, int quantita) {
        this.categoria = categoria;
        this.data = data;
        this.descrizione = descrizione;
        this.id = id;
        this.idAnimale = idAnimale;
        this.costoUnitario = costoUnitario;
        this.quantita = quantita;
    }

    public String getId() {
        return id;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getData() {
        return data;
    }

    public int getQuantita() {
        return quantita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public float getCostoUnitario() {
        return costoUnitario;
    }
}

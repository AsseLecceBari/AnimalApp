package model;

public class SpesaAnimale {
    private String categoria, data, quantita, descrizione;
    private int costoUnitario;

    public SpesaAnimale() {
    }

    public SpesaAnimale(String categoria, String data, String quantita, String descrizione, int costoUnitario) {
        this.categoria = categoria;
        this.data = data;
        this.quantita = quantita;
        this.descrizione = descrizione;
        this.costoUnitario = costoUnitario;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getData() {
        return data;
    }

    public String getQuantita() {
        return quantita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getCostoUnitario() {
        return costoUnitario;
    }
}

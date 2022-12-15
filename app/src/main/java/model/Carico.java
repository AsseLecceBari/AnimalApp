package model;

import android.widget.TextView;

public class Carico {
    private String id, dataInizio, dataFine, idAnimale, idProfessionista, note;
    private boolean isInCorso = true;

    public Carico(String id, String dataInizio, String dataFine, String idAnimale, String idProfessionista, String note, boolean isInCorso) {
        this.id = id;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.idAnimale = idAnimale;
        this.idProfessionista = idProfessionista;
        this.note = note;
        this.isInCorso = isInCorso;
    }

    public void setInCorso(boolean inCorso) {
        isInCorso = inCorso;
    }

    public Carico() {
    }

    public String getDataInizio() {
        return dataInizio;
    }

    public String getDataFine() {
        return dataFine;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getIdProfessionista() {
        return idProfessionista;
    }

    public String getNote() {
        return note;
    }

    public boolean isInCorso() {
        return isInCorso;
    }

    public String getId() {
        return id;
    }
}

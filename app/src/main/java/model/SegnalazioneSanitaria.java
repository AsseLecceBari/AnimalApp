package model;

public class SegnalazioneSanitaria {
    private String data;
    private String emailVet;
    private String motivoConsultazione;
    private String diagnosi;
    private String farmaci;
    private String trattamento;
    private String id;
    private String idAnimale;

    public SegnalazioneSanitaria() {
    }

    public SegnalazioneSanitaria(String data, String emailVet, String motivoConsultazione, String diagnosi, String farmaci, String trattamento, String id, String idAnimale) {
        this.data = data;
        this.emailVet = emailVet;
        this.motivoConsultazione = motivoConsultazione;
        this.diagnosi = diagnosi;
        this.farmaci = farmaci;
        this.trattamento = trattamento;
        this.id = id;
        this.idAnimale = idAnimale;
    }

    public String getIdAnimale() {
        return idAnimale;
    }

    public String getData() {
        return data;
    }

    public String getEmailVet() {
        return emailVet;
    }

    public String getMotivoConsultazione() {
        return motivoConsultazione;
    }

    public String getDiagnosi() {
        return diagnosi;
    }

    public String getFarmaci() {
        return farmaci;
    }

    public String getTrattamento() {
        return trattamento;
    }

    public String getId() {
        return id;
    }
}

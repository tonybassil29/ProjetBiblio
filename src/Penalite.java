import java.util.Date;

public class Penalite {
    private int idPenalite;
    private int idMembre;
    private String raison;
    private Date datePenalite;
    private int niveauPenalite;

    public Penalite(int idPenalite, int idMembre, String raison, Date datePenalite, int niveauPenalite) {
        this.idPenalite = idPenalite;
        this.idMembre = idMembre;
        this.raison = raison;
        this.datePenalite = datePenalite;
        this.niveauPenalite = niveauPenalite;
    }

    public int getNiveauPenalite () {
    	return niveauPenalite;
    }
    public int getIdPenalite() {
        return idPenalite;
    }

    public void setIdPenalite(int idPenalite) {
        this.idPenalite = idPenalite;
    }

    public int getIdMembre() {
        return idMembre;
    }

    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public Date getDatePenalite() {
        return datePenalite;
    }

    public void setDatePenalite(Date datePenalite) {
        this.datePenalite = datePenalite;
    }
    
    public void setNiveauPenalite (int niveauPenalite) {
    	this.niveauPenalite = niveauPenalite;
    }

    @Override
    public String toString() {
        return "Penalite{" +
                "idPenalite=" + idPenalite +
                ", idMembre=" + idMembre +
                ", raison='" + raison + '\'' +
                ", datePenalite=" + datePenalite +
                ", niveauPenalite=" + niveauPenalite +
                '}';
    }
}

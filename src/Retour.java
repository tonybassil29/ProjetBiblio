import java.util.Date;

public class Retour {
    private int idRetour;
    private int idEmprunt;
    private Date dateRetourReelle;
    private Date dateRetourPrevue;

    public Retour(int idRetour, int idEmprunt, Date dateRetourReelle, Date dateRetourPrevue) {
        this.idRetour = idRetour;
        this.idEmprunt = idEmprunt;
        this.dateRetourReelle = dateRetourReelle;
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public int getIdRetour() {
        return idRetour;
    }

    public void setIdRetour(int idRetour) {
        this.idRetour = idRetour;
    }

    public int getIdEmprunt() {
        return idEmprunt;
    }

    public void setIdEmprunt(int idEmprunt) {
        this.idEmprunt = idEmprunt;
    }

    public Date getDateRetourReelle() {
        return dateRetourReelle;
    }

    public void setDateRetourReelle(Date dateRetourReelle) {
        this.dateRetourReelle = dateRetourReelle;
    }

    public Date getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(Date dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    @Override
    public String toString() {
        return "Retour{" +
                "idRetour=" + idRetour +
                ", idEmprunt=" + idEmprunt +
                ", dateRetourReelle=" + dateRetourReelle +
                ", dateRetourPrevue=" + dateRetourPrevue +
                '}';
    }
}

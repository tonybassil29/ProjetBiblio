import java.util.Date;

public class Emprunt {
    private int idEmprunt;
    private int idLivre;
    private int idMembre;
    private Date dateEmprunt;

    public Emprunt(int idEmprunt, int idLivre, int idMembre, Date dateEmprunt) {
        this.idEmprunt = idEmprunt;
        this.idLivre = idLivre;
        this.idMembre = idMembre;
        this.dateEmprunt = dateEmprunt;
    }

    public int getIdEmprunt() {
        return idEmprunt;
    }

    public void setIdEmprunt(int idEmprunt) {
        this.idEmprunt = idEmprunt;
    }

    public int getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(int idLivre) {
        this.idLivre = idLivre;
    }

    public int getIdMembre() {
        return idMembre;
    }

    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }

    public Date getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(Date dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    @Override
    public String toString() {
        return "Emprunt{" +
                "idEmprunt=" + idEmprunt +
                ", idLivre=" + idLivre +
                ", idMembre=" + idMembre +
                ", dateEmprunt=" + dateEmprunt +
                '}';
    }
}

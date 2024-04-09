import java.util.Date;

public class Avis {
    private int idLivre;
    private int idMembre; // Ajout du champ idMembre
    private int note;
    private String commentaire;
    private Date dateAvis;

    public Avis(int idLivre, int idMembre, int note, String commentaire, Date dateAvis) {
        this.idLivre = idLivre;
        this.idMembre = idMembre; // Initialisation du champ idMembre
        this.note = note;
        this.commentaire = commentaire;
        this.dateAvis = dateAvis;
    }

    // Getters
    public int getIdLivre() { return idLivre; }
    public int getIdMembre() { return idMembre; } // Getter pour idMembre
    public int getNote() { return note; }
    public String getCommentaire() { return commentaire; }
    public Date getDateAvis() { return dateAvis; }
}


import java.util.Date;

public class EmpruntDetaille {
    private int idEmprunt;
    private String titre;
    private String auteur;
    private String editeur;
    private int anneePublication;
    private String genre;
    private String isbn;
    private Date dateEmprunt;
    private Date dateRetourPrevue;
    private boolean estRendu;

    public EmpruntDetaille(int idEmprunt, String titre, String auteur, String editeur, int anneePublication, 
                           String genre, String isbn, Date dateEmprunt, Date dateRetourPrevue, boolean estRendu) {
        this.idEmprunt = idEmprunt;
        this.titre = titre;
        this.auteur = auteur;
        this.editeur = editeur;
        this.anneePublication = anneePublication;
        this.genre = genre;
        this.isbn = isbn;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.estRendu = estRendu;
    }

    public void setIdEmprunt(int idEmprunt) {
		this.idEmprunt = idEmprunt;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public void setEditeur(String editeur) {
		this.editeur = editeur;
	}

	public void setAnneePublication(int anneePublication) {
		this.anneePublication = anneePublication;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setDateEmprunt(Date dateEmprunt) {
		this.dateEmprunt = dateEmprunt;
	}

	public void setDateRetourPrevue(Date dateRetourPrevue) {
		this.dateRetourPrevue = dateRetourPrevue;
	}

	public void setEstRendu(boolean estRendu) {
		this.estRendu = estRendu;
	}

	// Getters
    public int getIdEmprunt() { 
    	return idEmprunt; 
    	}
    public String getTitre() { 
    	return titre; 
    	}
    public String getAuteur() {
    	return auteur; 
    	}
    public String getEditeur() {
    	return editeur; 
    	}
    public int getAnneePublication() {
    	return anneePublication; 
    	}
    public String getGenre() { 
    	return genre; 
    	}
    public String getIsbn() { 
    	return isbn; 
    	}
    public Date getDateEmprunt() { 
    	return dateEmprunt; 
    	}
    public Date getDateRetourPrevue() { 
    	return dateRetourPrevue; 
    	}
    public boolean isEstRendu() { 
    	return estRendu; 
    	}
    
    public String toString() {
        return "EmpruntDetaille{" +
                "idEmprunt=" + idEmprunt +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", editeur='" + editeur + '\'' +
                ", anneePublication=" + anneePublication +
                ", genre='" + genre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", dateEmprunt=" + dateEmprunt +
                ", dateRetourPrevue=" + dateRetourPrevue +
                ", estRendu=" + estRendu +
                '}';
    }
}

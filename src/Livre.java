public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private int anneePublication;
    private String genre;
    private String isbn;
    private String editeur;
    private int quantiteDisponible;
    private String image;
    private boolean status;

    public Livre(int id, String titre, String auteur, int anneePublication, String genre, String isbn, String editeur, int quantiteDisponible, String image, boolean status) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.anneePublication = anneePublication;
        this.genre = genre;
        this.isbn = isbn;
        this.editeur = editeur;
        this.quantiteDisponible = quantiteDisponible;
        this.image = image;
        this.status = status;
    }

    public boolean getStatus() {
    	return status;
    }
    
    
    // Getters simples
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
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

    public String getEditeur() {
        return editeur;
    }

    public int getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public String getImage() {
        return image;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
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

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public void setQuantiteDisponible(int quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    
}

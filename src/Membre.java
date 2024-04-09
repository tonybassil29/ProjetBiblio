public class Membre {
    private int id;
    private String nom;
    private String prenom;
    private String adresse;
    private String email;
    private String role;
    public int status;

    public Membre(int id, String nom, String prenom, String adresse, String email, String role, int status) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    // Getters simples
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public int getStatus() {
    	return status;
    }
    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public void setStatus(int status) {
    	this.status = status;
    }
    
    
}

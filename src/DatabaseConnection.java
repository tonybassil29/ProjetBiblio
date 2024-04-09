import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Calendar;




import com.mysql.cj.xdevapi.Statement;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String dbUrl = "jdbc:mysql://localhost/biblio?serverTimezone=UTC&useSSL=false";
            String username = "root";
            String password = "";
            return DriverManager.getConnection(dbUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static List<Livre> getLivres() {
        List<Livre> livres = new ArrayList<>();
        String query = "SELECT * FROM livre where status = 1"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                livres.add(new Livre(
                    rs.getInt("id_livre"),
                    rs.getString("titre"),
                    rs.getString("auteur"),
                    rs.getInt("annee_publication"),
                    rs.getString("genre"),
                    rs.getString("isbn"),
                    rs.getString("editeur"),
                    rs.getInt("quantite_disponible"),
                    rs.getString("image"),
                    rs.getBoolean("status")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return livres;
    }
    
    public static List<Livre> getLivres2() {
        List<Livre> livres = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Livre");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id_livre");
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");
                int anneePublication = resultSet.getInt("annee_publication");
                String genre = resultSet.getString("genre");
                String isbn = resultSet.getString("isbn");
                String editeur = resultSet.getString("editeur");
                int quantiteDisponible = resultSet.getInt("quantite_disponible");
                String image = resultSet.getString("image");
                boolean status = resultSet.getBoolean("status");

                Livre livre = new Livre(id, titre, auteur, anneePublication, genre, isbn, editeur, quantiteDisponible, image, status);
                livres.add(livre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return livres;
    }
	    
    public static void updateLivreStatus(int livreId, boolean status) {
        String query = "UPDATE livre SET status = ? WHERE id_livre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBoolean(1, status);
            pstmt.setInt(2, livreId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void updateLivre(int livreId, String titre, String auteur, int anneePublication, String genre, String isbn, String editeur, int quantiteDisponible, boolean status) {
        String query = "UPDATE livre SET titre = ?, auteur = ?, annee_publication = ?, genre = ?, isbn = ?, editeur = ?, quantite_disponible = ?, status = ? WHERE id_livre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, auteur);
            pstmt.setInt(3, anneePublication);
            pstmt.setString(4, genre);
            pstmt.setString(5, isbn);
            pstmt.setString(6, editeur);
            pstmt.setInt(7, quantiteDisponible);
            pstmt.setBoolean(8, status);
            pstmt.setInt(9, livreId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static int insertLivre(String titre, String auteur, int anneePublication, String genre, String isbn, String editeur, int quantiteDisponible, boolean status) {
        String query = "INSERT INTO livre (titre, auteur, annee_publication, genre, isbn, editeur, quantite_disponible, image, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, titre);
            pstmt.setString(2, auteur);
            pstmt.setInt(3, anneePublication);
            pstmt.setString(4, genre);
            pstmt.setString(5, isbn);
            pstmt.setString(6, editeur);
            pstmt.setInt(7, quantiteDisponible);
            pstmt.setString(8, ""); // Laissez l'emplacement de l'image vide pour le moment
            pstmt.setBoolean(9, status);

            pstmt.executeUpdate();

            // Récupérer l'ID du livre nouvellement inséré
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newBookId = rs.getInt(1);
                    // Générer le chemin de l'image
                    String imagePath = "images/" + newBookId + ".png";
                    // Mettre à jour le chemin de l'image dans la base de données
                    updateLivreImagePath(newBookId, imagePath);

                    return newBookId;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Retourner -1 si l'ID n'a pas pu être récupéré
    }

    private static void updateLivreImagePath(int livreId, String imagePath) {
        String query = "UPDATE livre SET image = ? WHERE id_livre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imagePath);
            pstmt.setInt(2, livreId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static List<Emprunt> getEmprunts() {
        List<Emprunt> emprunts = new ArrayList<>();
        String query = "SELECT * FROM emprunt";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idEmprunt = rs.getInt("id_emprunt");
                int idLivre = rs.getInt("id_livre");
                int idMembre = rs.getInt("id_membre");
                Date dateEmprunt = rs.getDate("date_emprunt");

                Emprunt emprunt = new Emprunt(idEmprunt, idLivre, idMembre, dateEmprunt);
                emprunts.add(emprunt);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return emprunts;
    }




	    // Ajoutez cette méthode à votre classe DatabaseConnection
	    public static void deleteLivre(int livreId) {
	        String query = "DELETE FROM livre WHERE id_livre = ?";
	        try (Connection conn = getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(query)) {
	            pstmt.setInt(1, livreId);
	            pstmt.executeUpdate();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	    public static Map<Integer, Integer> getNombreEmpruntsParLivre() {
	        Map<Integer, Integer> nombreEmpruntsMap = new HashMap<>();
	        String query = "SELECT id_livre, COUNT(*) AS nombre_emprunts FROM emprunt GROUP BY id_livre";
	        try (Connection conn = getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(query);
	             ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {
	                int idLivre = rs.getInt("id_livre");
	                int nombreEmprunts = rs.getInt("nombre_emprunts");
	                nombreEmpruntsMap.put(idLivre, nombreEmprunts);
	               
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return nombreEmpruntsMap;
	    }
    public static List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        String query = "SELECT DISTINCT genre FROM livre";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return genres;
    }
    
    
    
    public static List<String> getEditeurs() {
        List<String> editeurs = new ArrayList<>();
        String query = "SELECT DISTINCT editeur FROM livre";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                editeurs.add(rs.getString("editeur"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return editeurs;
    }


    public static List<Membre> getMembres() {
        List<Membre> membres = new ArrayList<>();
        String query = "SELECT * FROM membre";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                membres.add(new Membre(
                    rs.getInt("id_membre"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getInt("statut")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return membres;
    }

    public static void deleteMembre(int idMembre) {
        String query = "DELETE FROM membre WHERE id_membre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idMembre);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void updateMembre(Membre membre) {
    	String query = "UPDATE membre SET nom=?, prenom=?, adresse=?, email=?, role=?, statut=? WHERE id_membre=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, membre.getNom());
            pstmt.setString(2, membre.getPrenom());
            pstmt.setString(3, membre.getAdresse());
            pstmt.setString(4, membre.getEmail());
            pstmt.setString(5, membre.getRole());
            pstmt.setInt(6, membre.getStatus());
            pstmt.setInt(7, membre.getId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    
    public static List<Retour> getRetours() {
        List<Retour> retours = new ArrayList<>();
        String query = "SELECT * FROM retour";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idRetour = rs.getInt("id_retour");
                int idEmprunt = rs.getInt("id_emprunt");
                Date dateRetourReelle = rs.getDate("date_retour_reelle");
                Date dateRetourPrevue = rs.getDate("date_retour_prevue");

                Retour retour = new Retour(idRetour, idEmprunt, dateRetourReelle, dateRetourPrevue);
                retours.add(retour);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return retours;
    }

    public static void updateRetourDateRetourPrevue(int retourId, Date newDate) {
        String query = "UPDATE retour SET date_retour_prevue = ? WHERE id_retour = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, new java.sql.Date(newDate.getTime()));
            pstmt.setInt(2, retourId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static Map<Integer, Integer> getNombreLivresEmpruntesParMembre() {
        Map<Integer, Integer> nombreLivresEmpruntesMap = new HashMap<>();
        String query = "SELECT id_membre, COUNT(*) AS nombre_livres_empruntes FROM emprunt GROUP BY id_membre";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idMembre = rs.getInt("id_membre");
                int nombreLivresEmpruntes = rs.getInt("nombre_livres_empruntes");
                nombreLivresEmpruntesMap.put(idMembre, nombreLivresEmpruntes);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return nombreLivresEmpruntesMap;
    }

    public static List<Penalite> getPenalites() {
        List<Penalite> penalites = new ArrayList<>();
        String query = "SELECT * FROM penalite";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int idPenalite = rs.getInt("id_penalite");
                int idMembre = rs.getInt("id_membre");
                String raison = rs.getString("raison");
                Date datePenalite = rs.getDate("date_penalite");
                int niveauPenalite = rs.getInt("niveau");

                Penalite penalite = new Penalite(idPenalite, idMembre, raison, datePenalite, niveauPenalite);
                penalites.add(penalite);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return penalites;
    }
    
    public static void validerPanier(List<Livre> panierLivres, int idMembre) {
        String queryEmprunt = "INSERT INTO emprunt (id_livre, id_membre, date_emprunt) VALUES (?, ?, ?)";
        String queryRetour = "INSERT INTO retour (id_emprunt, date_retour_prevue) VALUES (?, ?)";
        String queryUpdateQuantite = "UPDATE livre SET quantite_disponible = quantite_disponible - 1 WHERE id_livre = ? AND quantite_disponible > 0";

        // Utilisez la date actuelle pour l'emprunt
        java.sql.Date dateEmprunt = new java.sql.Date(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateEmprunt);
        cal.add(Calendar.DATE, 14);
        java.sql.Date dateRetourPrevue = new java.sql.Date(cal.getTimeInMillis());

        try (Connection conn = getConnection()) {
            // Désactiver l'autocommit pour utiliser une transaction
            conn.setAutoCommit(false);
            
            try {
                for (Livre livre : panierLivres) {
                    // Insérer un emprunt
                    try (PreparedStatement pstmtEmprunt = conn.prepareStatement(queryEmprunt, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        pstmtEmprunt.setInt(1, livre.getId());
                        pstmtEmprunt.setInt(2, idMembre);
                        pstmtEmprunt.setDate(3, dateEmprunt);
                        pstmtEmprunt.executeUpdate();

                        // Récupérer l'id_emprunt généré
                        try (ResultSet rs = pstmtEmprunt.getGeneratedKeys()) {
                            if (rs.next()) {
                                int idEmprunt = rs.getInt(1);

                                // Insérer un retour avec l'id_emprunt récupéré
                                try (PreparedStatement pstmtRetour = conn.prepareStatement(queryRetour)) {
                                    pstmtRetour.setInt(1, idEmprunt);
                                    pstmtRetour.setDate(2, dateRetourPrevue);
                                    pstmtRetour.executeUpdate();
                                }

                                // Mettre à jour la quantité disponible du livre
                                try (PreparedStatement pstmtUpdateQuantite = conn.prepareStatement(queryUpdateQuantite)) {
                                    pstmtUpdateQuantite.setInt(1, livre.getId());
                                    pstmtUpdateQuantite.executeUpdate();
                                }
                            }
                        }
                    }
                }
                // Commit les modifications si tout s'est bien passé
                conn.commit();
            } catch (SQLException ex) {
                // En cas d'erreur, rollback la transaction
                conn.rollback();
                throw ex; // Rethrow l'exception pour la gestion des erreurs plus haut dans l'appelant
            } finally {
                // Réactiver l'autocommit à la fin
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Gestion de l'erreur, peut-être renvoyer une indication d'échec à l'utilisateur
        }
    }
    
    
    public static List<EmpruntDetaille> getEmpruntsDetaillesParMembre(int idMembre) {
        List<EmpruntDetaille> emprunts = new ArrayList<>();
        // Ajout de la clause ORDER BY pour trier les résultats, en mettant les emprunts non retournés en premier
        String query = "SELECT e.id_emprunt, l.titre, l.auteur, l.editeur, l.annee_publication, l.genre, l.isbn, e.date_emprunt, " +
                       "r.date_retour_prevue, r.date_retour_reelle " +
                       "FROM emprunt e " +
                       "JOIN livre l ON e.id_livre = l.id_livre " +
                       "LEFT JOIN retour r ON e.id_emprunt = r.id_emprunt " +
                       "WHERE e.id_membre = ? " +
                       "ORDER BY r.date_retour_reelle IS NULL DESC, r.date_retour_reelle ASC, e.date_emprunt DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idMembre);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idEmprunt = rs.getInt("id_emprunt");
                    String titre = rs.getString("titre");
                    String auteur = rs.getString("auteur");
                    String editeur = rs.getString("editeur");
                    int anneePublication = rs.getInt("annee_publication");
                    String genre = rs.getString("genre");
                    String isbn = rs.getString("isbn");
                    Date dateEmprunt = rs.getDate("date_emprunt");
                    Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                    boolean estRendu = rs.getDate("date_retour_reelle") != null;

                    EmpruntDetaille empruntDetaille = new EmpruntDetaille(
                            idEmprunt, titre, auteur, editeur, anneePublication, 
                            genre, isbn, dateEmprunt, dateRetourPrevue, estRendu
                    );
                    emprunts.add(empruntDetaille);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return emprunts;
    }

    
    public static void retournerLivre(int idEmprunt) {
        // Mettre à jour la table retour pour marquer le livre comme retourné
        String queryRetour = "UPDATE retour SET date_retour_reelle = CURRENT_DATE() WHERE id_emprunt = ?";
        
        // Incrémenter la quantité disponible pour le livre associé
        String queryIncrQuantite = "UPDATE livre SET quantite_disponible = quantite_disponible + 1 "
                                  + "WHERE id_livre = (SELECT id_livre FROM emprunt WHERE id_emprunt = ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmtRetour = conn.prepareStatement(queryRetour);
             PreparedStatement pstmtIncrQuantite = conn.prepareStatement(queryIncrQuantite)) {
            
            // Marque le livre comme retourné
            pstmtRetour.setInt(1, idEmprunt);
            int affectedRowsRetour = pstmtRetour.executeUpdate();

            if (affectedRowsRetour == 0) {
                System.out.println("Aucune mise à jour pour le retour. Vérifiez l'ID d'emprunt.");
                return; // Sortir de la méthode si aucune mise à jour n'a eu lieu
            }

            // Incrémenter la quantité disponible du livre
            pstmtIncrQuantite.setInt(1, idEmprunt);
            int affectedRowsQuantite = pstmtIncrQuantite.executeUpdate();

            if (affectedRowsQuantite > 0) {
                System.out.println("La quantité disponible du livre a été incrémentée.");
            } else {
                System.out.println("Échec de l'incrémentation de la quantité disponible. Vérifiez l'ID d'emprunt.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void ajouterCommentaire(int idEmprunt, String commentaire, int note) {
        String sql = "INSERT INTO avis (id_livre, id_membre, note, commentaire, date_avis) " +
                     "SELECT id_livre, id_membre, ?, ?, NOW() FROM emprunt WHERE id_emprunt = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, note); // Ajouter la note en tant que premier paramètre
            pstmt.setString(2, commentaire); // Le commentaire est maintenant le deuxième paramètre
            pstmt.setInt(3, idEmprunt); // ID de l'emprunt comme troisième paramètre
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Avis> getAvisPourLivre(int idLivre) {
        List<Avis> avisList = new ArrayList<>();
        String sql = "SELECT id_membre, note, commentaire, date_avis FROM avis WHERE id_livre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLivre);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idMembre = rs.getInt("id_membre");
                    int note = rs.getInt("note");
                    String commentaire = rs.getString("commentaire");
                    Date dateAvis = new Date(rs.getTimestamp("date_avis").getTime()); // Conversion de Timestamp en Date
                    avisList.add(new Avis(idLivre, idMembre, note, commentaire, dateAvis));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avisList;
    }

    
    public static List<Livre> getRecommendedBooks(int idMembre) {
        List<Livre> recommendedBooks = new ArrayList<>();
        List<Livre> borrowedBooks = getBorrowedBooksByMember(idMembre);

        if (!borrowedBooks.isEmpty()) {
            Map<String, Integer> genreCountMap = new HashMap<>();

            // Count the number of books borrowed per genre
            for (Livre borrowedBook : borrowedBooks) {
                String genre = borrowedBook.getGenre();
                genreCountMap.put(genre, genreCountMap.getOrDefault(genre, 0) + 1);
                
            }
            

            // Find genres with more than one book borrowed
            List<String> eligibleGenres = genreCountMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        

            if (!eligibleGenres.isEmpty()) {
                // Get books from the eligible genres
                for (String genre : eligibleGenres) {
                    List<Livre> genreBooks = getLivresByGenre(genre);
                    
                    // Filter out books already borrowed
                    for (Livre book : genreBooks) {
                        boolean alreadyBorrowed = borrowedBooks.stream().anyMatch(borrowedBook -> borrowedBook.getId() == book.getId());
                        if (!alreadyBorrowed) {
                            recommendedBooks.add(book);
                        }
                    }
                }
            
            }
        }

        return recommendedBooks;
    }

    private static List<Livre> getBorrowedBooksByMember(int idMembre) {
        List<Livre> borrowedBooks = new ArrayList<>();
        String query = "SELECT * FROM livre l JOIN emprunt e ON l.id_livre = e.id_livre WHERE e.id_membre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idMembre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    borrowedBooks.add(new Livre(
                            rs.getInt("id_livre"),
                            rs.getString("titre"),
                            rs.getString("auteur"),
                            rs.getInt("annee_publication"),
                            rs.getString("genre"),
                            rs.getString("isbn"),
                            rs.getString("editeur"),
                            rs.getInt("quantite_disponible"),
                            rs.getString("image"),
                            rs.getBoolean("status")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return borrowedBooks;
    }

    private static List<Livre> getLivresByGenre(String genre) {
        List<Livre> genreBooks = new ArrayList<>();
        String query = "SELECT * FROM livre WHERE genre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, genre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genreBooks.add(new Livre(
                            rs.getInt("id_livre"),
                            rs.getString("titre"),
                            rs.getString("auteur"),
                            rs.getInt("annee_publication"),
                            rs.getString("genre"),
                            rs.getString("isbn"),
                            rs.getString("editeur"),
                            rs.getInt("quantite_disponible"),
                            rs.getString("image"),
                            rs.getBoolean("status")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return genreBooks;
    }

    public static List<Livre> getRecommendedBooksByAuthor(int idMembre) {
        List<Livre> recommendedBooks = new ArrayList<>();
        List<Livre> borrowedBooks = getBorrowedBooksByMember(idMembre);

        if (!borrowedBooks.isEmpty()) {
            Map<String, Integer> authorCountMap = new HashMap<>();

            // Count the number of books borrowed per author
            for (Livre borrowedBook : borrowedBooks) {
                String author = borrowedBook.getAuteur();
                authorCountMap.put(author, authorCountMap.getOrDefault(author, 0) + 1);
            }

            // Find authors with more than one book borrowed
            List<String> eligibleAuthors = authorCountMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!eligibleAuthors.isEmpty()) {
                // Get books from the eligible authors
                for (String author : eligibleAuthors) {
                    List<Livre> authorBooks = getLivresByAuthor(author);

                    // Filter out books already borrowed
                    for (Livre book : authorBooks) {
                        boolean alreadyBorrowed = borrowedBooks.stream().anyMatch(borrowedBook -> borrowedBook.getId() == book.getId());
                        if (!alreadyBorrowed) {
                            recommendedBooks.add(book);
                        }
                    }
                }
            }
        }

        return recommendedBooks;
    }
    public static void mettreAJourNiveauPenalite(int idMembre, int nouveauNiveau) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection(); // Obtenez une connexion à la base de données
            // Mettre à jour le niveau de pénalité dans la base de données
            String sql = "UPDATE penalite SET niveau = ? WHERE id_membre = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nouveauNiveau);
            pstmt.setInt(2, idMembre);
            pstmt.executeUpdate();
        } finally {
            // Fermez les ressources dans un bloc finally pour garantir qu'elles sont toujours fermées
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


    private static List<Livre> getLivresByAuthor(String author) {
        List<Livre> authorBooks = new ArrayList<>();
        String query = "SELECT * FROM livre WHERE auteur = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, author);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    authorBooks.add(new Livre(
                            rs.getInt("id_livre"),
                            rs.getString("titre"),
                            rs.getString("auteur"),
                            rs.getInt("annee_publication"),
                            rs.getString("genre"),
                            rs.getString("isbn"),
                            rs.getString("editeur"),
                            rs.getInt("quantite_disponible"),
                            rs.getString("image"),
                            rs.getBoolean("status")
                    ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return authorBooks;
    }

    public void verifierEtAjouterPenalites() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            // Récupérer les emprunts en retard
            String sql = "SELECT r.id_retour, r.id_emprunt, e.id_membre, r.date_retour_prevue " +
                    "FROM retour r " +
                    "INNER JOIN emprunt e ON r.id_emprunt = e.id_emprunt " +
                    "WHERE r.date_retour_reelle IS NULL AND r.date_retour_prevue < ?";


            pstmt = conn.prepareStatement(sql);
            // Supposons que getCurrentDate() retourne un objet java.util.Date
            java.util.Date utilDate = getCurrentDate();
            
            // Convertir java.util.Date en java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            // Utiliser la java.sql.Date dans votre PreparedStatement
            pstmt.setDate(1, sqlDate);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                int idEmprunt = rs.getInt("id_emprunt");
                int idMembre = rs.getInt("id_membre");
                Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                
                // Calculer le nombre de jours de retard
                int joursRetard = calculerJoursRetard(dateRetourPrevue);
                
               
                // Vérifier le niveau de pénalité
                if (joursRetard > 0 && joursRetard < 7) {
                    if (!penaliteExiste(conn, idMembre, 1)) {
                        ajouterPenalite(conn, idMembre, "test", 1);
                    }
                } else if (joursRetard >= 7 && joursRetard <= 9) {
                    if (!penaliteExiste(conn, idMembre, 2)) {
                        ajouterPenalite(conn, idMembre, "test", 2);
                    }
                } else if (joursRetard > 9) {
                    if (!penaliteExiste(conn, idMembre, 3)) {
                        ajouterPenalite(conn, idMembre, "test", 3);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    
   
    public static boolean penaliteExiste(Connection conn, int idMembre, int niveau) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM penalite WHERE id_membre = ? AND niveau = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idMembre);
        pstmt.setInt(2, niveau);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt("count");
            return count > 0;
        }
        return false;
    }

    public static Map<Integer, Integer> getNombrePenalitesParMembre() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<Integer, Integer> nombrePenalitesMap = new HashMap<>();

        try {
            conn = getConnection();
            String sql = "SELECT id_membre, COUNT(*) AS count FROM penalite GROUP BY id_membre";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int idMembre = rs.getInt("id_membre");
                int count = rs.getInt("count");
                nombrePenalitesMap.put(idMembre, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return nombrePenalitesMap;
    }

    private static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int calculerJoursRetard(Date dateRetourPrevue) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTime().getTime();
        long expectedReturnTime = dateRetourPrevue.getTime();
        long diff = currentTime - expectedReturnTime;
        return (int) (diff / (24 * 60 * 60 * 1000));
    }
 // Méthode pour ajouter une pénalité
    private void ajouterPenalite(Connection conn, int idMembre, String raison, int niveau) throws SQLException {
        // Supprimer les pénalités de niveaux inférieurs
        for (int i = 1; i < niveau; i++) {
            supprimerPenalite( idMembre);
        }

        // Ajouter la nouvelle pénalité
        String sql = "INSERT INTO penalite (id_membre, raison, date_penalite, niveau) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idMembre);
        pstmt.setString(2, raison);
     // Supposons que getCurrentDate() retourne un objet java.util.Date
        java.util.Date utilDate = getCurrentDate();

        // Convertir java.util.Date en java.sql.Date
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        // Utiliser la java.sql.Date dans votre PreparedStatement
        pstmt.setDate(3, sqlDate);

        pstmt.setInt(4, niveau);
        pstmt.executeUpdate();
        pstmt.close();
        
        // Si c'est le niveau 3, bloquer le compte
        if (niveau == 3) {
            bloquerCompte(conn, idMembre);
        }
    }


    // Méthode pour supprimer une pénalité d'un certain niveau pour un utilisateur
    private void supprimerPenalite(Connection conn, int idMembre, int niveau) throws SQLException {
        String sql = "DELETE FROM penalite WHERE id_membre = ? AND niveau = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idMembre);
        pstmt.setInt(2, niveau);
        pstmt.executeUpdate();
        pstmt.close();
    }

    // Méthode pour bloquer le compte d'un utilisateur
    private void bloquerCompte(Connection conn, int idMembre) throws SQLException {
        String sql = "UPDATE membre SET statut = 0 WHERE id_membre = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idMembre);
        pstmt.executeUpdate();
        pstmt.close();
    }

    // Méthode pour obtenir la date actuelle
    private Date getCurrentDate() {
        return new Date(Calendar.getInstance().getTime().getTime());
    }
    
    public static Map<Integer, Integer> recupererNiveauPenalitesPourUtilisateursConnectes(int userId) {
        Map<Integer, Integer> niveauPenalites = new HashMap<>();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT id_membre, MAX(niveau) AS niveau_max FROM penalite WHERE id_membre = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                int idMembre = rs.getInt("id_membre");
                int niveauMax = rs.getInt("niveau_max");
                niveauPenalites.put(idMembre, niveauMax);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return niveauPenalites;
    }
    
    public void verifierEtSupprimerPenalitesUtilisateurConnecte(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            // Récupérer les pénalités associées aux retours avec date_retour_reelle non NULL pour l'utilisateur connecté
            String sql = "SELECT p.id_penalite " +
                    "FROM penalite p " +
                    "INNER JOIN emprunt e ON p.id_membre = e.id_membre " +
                    "INNER JOIN retour r ON e.id_emprunt = r.id_emprunt " +
                    "WHERE e.id_membre = ? AND r.date_retour_reelle IS NOT NULL";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                int idPenalite = rs.getInt("id_penalite");
                // Supprimer la pénalité associée à cet utilisateur
                supprimerPenalite(idPenalite);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

 public static void supprimerPenalite(int idPenalite) throws SQLException {
	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    try {
	        conn = DatabaseConnection.getConnection(); // Obtenez une connexion à la base de données
	        String sql = "DELETE FROM penalite WHERE id_penalite = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, idPenalite);
	        pstmt.executeUpdate();
	    } finally {
	        // Fermez les ressources dans un bloc finally pour garantir qu'elles sont toujours fermées
	        if (pstmt != null) {
	            pstmt.close();
	        }
	        if (conn != null) {
	            conn.close();
	        }
	    }
	}



}

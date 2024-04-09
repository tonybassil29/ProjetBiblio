import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Separator;



public class MainPage {
    private static TextField rechercheField;
    private static List<CheckBox> genreCheckBoxes;
    private static List<CheckBox> editeurCheckBoxes;
    private static GridPane grid;
    private static List<Livre> livres;
    private static boolean genresVisible = false;
    private static boolean editeursVisible = false;
    private static boolean messageVisible = true; 
    private static List<Livre> panierLivres = new ArrayList<>();
    private static Stage panierStage;

    // Méthode pour obtenir le livre sélectionné
    private static Livre getLivreSelectionne(ListView<Livre> livresList) {
        Livre livreSelectionne = null;
        if (livresList != null && livresList.getSelectionModel().getSelectedItem() != null) {
            livreSelectionne = livresList.getSelectionModel().getSelectedItem();
        }
        return livreSelectionne;
    }


    private static void ajouterAuPanier(Livre livre) {
        // Vérifie si le livre est déjà dans le panier
        boolean livreDejaDansPanier = panierLivres.contains(livre);
        if (!livreDejaDansPanier) {
            panierLivres.add(livre);
        } else {
            // Affiche un message à l'utilisateur indiquant qu'il ne peut pas emprunter le même livre plus d'une fois
        	Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle("Avertissement");
        	alert.setHeaderText("Livre déjà emprunté");
        	alert.setContentText("Vous avez déjà emprunté ce livre. Vous ne pouvez pas emprunter le même livre plus d'une fois.");
        	alert.show(); // Affiche l'alerte de manière non bloquante

        	// Met la fenêtre de panier au premier plan pour s'assurer que l'alerte est visible
        	panierStage.toFront();
        }
    }

    // Méthode pour afficher les titres des livres dans le panier
    private static void afficherPanier() {
        System.out.println("Contenu du panier :");
        for (Livre livre : panierLivres) {
            System.out.println(livre.getTitre());
        }
    }


    public static Scene createMainPageScene(Stage primaryStage, Scene mainScene) {
    	VBox layout = new VBox(20);
    	layout.setAlignment(Pos.CENTER);
    	layout.setPadding(new Insets(20));

    	HBox newHBox = new HBox();

    	newHBox.setPrefWidth(200);
    	newHBox.setPrefHeight(1000);

    	newHBox.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");

    	// Appel de la fonction LivreRecommande pour obtenir la HBox des livres recommandés
    	VBox livresRecommandesBox = LivreRecommande(primaryStage);
    	
    

    	    

    	// Ajout de la HBox des livres recommandés à la HBox newHBox
    	newHBox.getChildren().add(livresRecommandesBox);

    	// Création d'une HBox pour contenir la HBox newHBox
    	HBox searchBox1 = new HBox(10, newHBox);
    	searchBox1.setAlignment(Pos.CENTER_RIGHT);

    	
    	   
        DatabaseConnection databaseConnection = new DatabaseConnection();
       
   

        // Récupérer les niveaux de pénalité pour les utilisateurs connectés
        Map<Integer, Integer> niveauxPenalites = DatabaseConnection.recupererNiveauPenalitesPourUtilisateursConnectes(MainScreen.userId);


        // Vérifier les niveaux de pénalité et afficher les messages correspondants
        niveauxPenalites.forEach((idUtilisateur, niveauPenalite) -> {
            Label label = new Label();
            if (niveauPenalite == 1) {
                label.setText("VOUS NE POUVEZ PAS EMPRUNTER LES LIVRES RENDEZ LES LIVRES SOUS 7 JOURS");
                label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: red;");
            } else if (niveauPenalite == 2) {
                label.setText("VOUS NE POUVEZ PAS EMPRUNTER LES LIVRES RENDEZ LES LIVRES SOUS 2 JOURS");
                label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: red;");
            } else if (niveauPenalite == 3) {
                label.setText("Compte Bloqué");
                label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: red;"); 
                return;
            }
            layout.getChildren().add(label);
        });


        // Appeler la méthode verifierEtAjouterPenalites() sur l'instance de DatabaseConnection
        databaseConnection.verifierEtSupprimerPenalitesUtilisateurConnecte(MainScreen.userId);
        databaseConnection.verifierEtAjouterPenalites();
        
        
        
        Image searchIcon = new Image("search.png");
        ImageView searchIconView = new ImageView(searchIcon);
        searchIconView.setFitWidth(20);
        searchIconView.setFitHeight(20);

        rechercheField = new TextField();
        rechercheField.setMaxWidth(160);
        rechercheField.setMinWidth(160);
        
        Button infoButton = new Button("i");
        infoButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Création de la bulle d'information avec le message souhaité
        Tooltip tooltip = new Tooltip("Recherche par Titre, ISBN, Année Publication et Auteur");
        Tooltip.install(infoButton, tooltip); // Installation de la bulle d'information sur le bouton

        // Ajout d'un événement pour afficher la bulle d'information lorsque vous appuyez sur le bouton
        infoButton.setOnAction(e -> {
            if (messageVisible) {
                tooltip.show(infoButton, infoButton.localToScreen(0, 0).getX() + infoButton.getWidth(), infoButton.localToScreen(0, 0).getY());
            } else {
                tooltip.hide();
            }
            messageVisible = !messageVisible; // Inverser le statut de visibilité du message
        });
        Button logoButton = new Button();
        Image logoImage = new Image("shopping-cart.png");
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(20);
        logoImageView.setFitHeight(20);
        logoButton.setGraphic(logoImageView);
        logoButton.setOnAction(e -> {
            displayBonjourMessage(primaryStage); // Assurez-vous que primaryStage est accessible ici
        });
     
        
        Button empruntsButton = new Button("Mes emprunts");
        empruntsButton.setOnAction(event -> {
        	 Scene empruntScene = createEmpruntsScene.createEmpruntScenes(primaryStage, primaryStage.getScene());
             primaryStage.setScene(empruntScene);
             primaryStage.setFullScreen(true); 
        });


        
        
        Button logoutButton = new Button("Déconnexion");
        logoutButton.setOnAction(e -> {
            // Ici, nous changeons la scène pour revenir à l'écran principal.
            // Cela suppose que vous avez une méthode qui crée et retourne la scène principale.
        	primaryStage.setScene(mainScene); // Retournez à la scène principale si l'inscription est réussie
            primaryStage.setFullScreen(true);
        });

        HBox searchBox = new HBox(10, rechercheField, searchIconView, infoButton, logoButton,empruntsButton, logoutButton);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        
        

        
        livres = DatabaseConnection.getLivres();

       

        List<String> genres = new ArrayList<>();
        genres.add("Tous les genres");
        genres.add("Absurde");
        genres.add("Aventure");
        genres.add("Contemporain");
        genres.add("Dystopie");
        genres.add("Épopée");
        genres.add("Expérimental");
        genres.add("Existentialiste");
        genres.add("Fables");
        genres.add("Fantaisie");
        genres.add("Fantastique");
        genres.add("Guerre");
        genres.add("Historique");
        genres.add("Jeunesse");
        genres.add("Judiciaire");
        genres.add("Littérature jeunesse");
        genres.add("Philosophique");
        genres.add("Poésie");
        genres.add("Policier");
        genres.add("Préhistorique");
        genres.add("Psychologique");
        genres.add("Réaliste");
        genres.add("Roman classique");
        genres.add("Roman historique");
        genres.add("Roman policier");
        genres.add("Roman psychologique");
        genres.add("Roman réaliste");
        genres.add("Roman romantique");
        genres.add("Science-fiction");

        List<String> editeurs = new ArrayList<>();
        editeurs.add("Tous les éditeurs");
        editeurs.add("Bragelonne");
        editeurs.add("Editions Flammarion");
        editeurs.add("Éditeur");
        editeurs.add("Folio");
        editeurs.add("Folio Junior");
        editeurs.add("Gallimard");
        editeurs.add("Gallimard Jeunesse");
        editeurs.add("J'ai Lu");
        editeurs.add("La Volte");
        editeurs.add("Le Livre de Poche");
        editeurs.add("LGF");
        editeurs.add("Pocket");
        editeurs.add("Pocket Jeunesse");
        editeurs.add("Robert Laffont");
        editeurs.add("Rivages");

        genreCheckBoxes = new ArrayList<>();
        for (String genre : genres) {
            CheckBox checkBox = new CheckBox(genre);
            checkBox.setVisible(false); // Rendre invisible initialement
            genreCheckBoxes.add(checkBox);
        }

        editeurCheckBoxes = new ArrayList<>();
        for (String editeur : editeurs) {
            CheckBox checkBox = new CheckBox(editeur);
            checkBox.setVisible(false); // Rendre invisible initialement
            editeurCheckBoxes.add(checkBox);
        }
        
        for (CheckBox checkBox : genreCheckBoxes) {
            checkBox.setStyle("-fx-font-size: 10px; -fx-padding: 2 5 2 5;");
        }
        
        for (CheckBox checkBox : editeurCheckBoxes) {
            checkBox.setStyle("-fx-font-size: 10px; -fx-padding: 2 5 2 5;");
        }
        grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 0, 5, 0));

        populateGrid(grid, livres);
        
        VBox mainLayout = new VBox(20); // Vous pouvez ajuster l'espace entre les éléments si nécessaire
        layout.setAlignment(Pos.CENTER); // Centre tous les enfants de VBox
        layout.setPadding(new Insets(20));
        
        layout.getChildren().add(grid);


        for (CheckBox checkBox : genreCheckBoxes) {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                // Désélectionner "Tous les genres" si une autre case à cocher est sélectionnée
                if (!checkBox.getText().equals("Tous les genres") && newValue) {
                    for (CheckBox otherCheckBox : genreCheckBoxes) {
                        if (otherCheckBox.getText().equals("Tous les genres")) {
                            otherCheckBox.setSelected(false);
                            break;
                        }
                    }
                }
                filterLivres();
            });
        }

        for (CheckBox checkBox : editeurCheckBoxes) {
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                // Désélectionner "Tous les éditeurs" si une autre case à cocher est sélectionnée
                if (!checkBox.getText().equals("Tous les éditeurs") && newValue) {
                    for (CheckBox otherCheckBox : editeurCheckBoxes) {
                        if (otherCheckBox.getText().equals("Tous les éditeurs")) {
                            otherCheckBox.setSelected(false);
                            break;
                        }
                    }
                }
                filterLivres();
            });
        }

        double initialScrollPaneY = 0;

     // À l'intérieur de votre méthode...
     ScrollPane scrollPane = new ScrollPane(grid);
     scrollPane.setPrefViewportHeight(400);
     scrollPane.setMinViewportWidth(100);
     scrollPane.setMaxWidth(1050); // Set a maximum width here, e.g., 200 pixels
     scrollPane.setFitToWidth(true);
     scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
     scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
     initialScrollPaneY = scrollPane.getLayoutY(); // Sauvegarder la position initiale

     final double[] initialScrollPaneY1 = {scrollPane.getLayoutY()}; // Déclarer initialScrollPaneY comme un tableau d'une seule case

  // Dans vos actions d'événement
  Button genresButton = new Button("Genres \u25B6"); // Flèche vers la droite
  Button editeursButton = new Button("Éditeurs \u25B6"); // Flèche

  genresButton.setOnAction(e -> {
	    editeursVisible = false; // Ajoutez cette ligne pour s'assurer que la liste des éditeurs est cachée quand on affiche celle des genres
	    editeurCheckBoxes.forEach(checkBox -> checkBox.setVisible(false)); // Cache les CheckBox des éditeurs
	    genresVisible = !genresVisible;
	    genreCheckBoxes.forEach(checkBox -> checkBox.setVisible(genresVisible));
	    genresButton.setText(genresVisible ? "Genres \u25BC" : "Genres \u25B6"); // Change la flèche
	    editeursButton.setText("Éditeurs \u25B6"); // S'assure que la flèche des éditeurs est réinitialisée
	});

	editeursButton.setOnAction(e -> {
	    genresVisible = false; // Ajoutez cette ligne pour s'assurer que la liste des genres est cachée quand on affiche celle des éditeurs
	    genreCheckBoxes.forEach(checkBox -> checkBox.setVisible(false)); // Cache les CheckBox des genres
	    editeursVisible = !editeursVisible;
	    editeurCheckBoxes.forEach(checkBox -> checkBox.setVisible(editeursVisible));
	    editeursButton.setText(editeursVisible ? "Éditeurs \u25BC" : "Éditeurs \u25B6"); // Change la flèche
	    genresButton.setText("Genres \u25B6"); // S'assure que la flèche des genres est réinitialisée
	});



        
        HBox genresTitle = new HBox(5, genresButton);
        HBox editeursTitle = new HBox(5, editeursButton);

        // Création des grilles pour organiser les cases à cocher des genres et des éditeurs
        GridPane genresGrid = createCheckBoxGrid(genreCheckBoxes, 7);
        GridPane editeursGrid = createCheckBoxGrid(editeurCheckBoxes, 4);

        HBox buttonsBox = new HBox(10, genresButton, editeursButton);

        HBox checkBoxesAndSearchBox1Box = new HBox(10);
        checkBoxesAndSearchBox1Box.getChildren().addAll(genresGrid, editeursGrid, searchBox1);
        checkBoxesAndSearchBox1Box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchBox1, Priority.ALWAYS); // Permet à searchBox1 de s'étendre horizontalement
        HBox centeringBox = new HBox();
        centeringBox.setAlignment(Pos.CENTER); // Pour centrer le contenu horizontalement
        centeringBox.getChildren().add(scrollPane);
        HBox.setHgrow(centeringBox, Priority.ALWAYS); // Pour permettre à centeringBox de s'étendre horizontalement

     
        HBox.setMargin(centeringBox, new Insets(0, 0, 0, 0));
  
        Label labelLivresDisponibles = new Label("Livres disponibles:");
        Label labelRecommandations = new Label("Recommandations:");

    
   


        // Création de la HBox
        HBox hbox = new HBox(labelLivresDisponibles, labelRecommandations);
        hbox.setAlignment(Pos.CENTER);

        // Apply negative margin to move "Recommandations" 40 pixels to the left
        labelLivresDisponibles.setTranslateX(500);
        labelRecommandations.setPadding(new Insets(0, 0, 0, 1100));
        

        // Ajout de la HBox à votre VBox
        layout.getChildren().addAll(
            searchBox,
            buttonsBox,
            checkBoxesAndSearchBox1Box, // Ajouter la HBox contenant les cases à cocher et searchBox1
            hbox, // Ajouter la HBox contenant les labels "Livres disponibles" et "Recommandations"
            new HBox(centeringBox, searchBox1)
        );

        // Ajouter la HBox contenant les cases à cocher et searchBox1 à la partie supérieure du VBox
       
        Scene scene = new Scene(layout, 900, 600);

        rechercheField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLivres();
        });

        return scene;
    }


    private static void animateScrollPane(Node node, double fromY, double toY) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), node);
        transition.setFromY(fromY);
        transition.setToY(toY);
        transition.play();
    }
    private static void populateGrid(GridPane grid, List<Livre> livres) {
        grid.getChildren().clear(); // Clear existing grid
        int col = 0;
        int row = 0;
        for (Livre livre : livres) {
            VBox livreBox = createLivreBox(livre);
            livreBox.setOnMouseClicked(e -> showBookDetails(livre)); // Ajout de l'événement de clic pour afficher les détails du livre
            grid.add(livreBox, col, row);
            col++;
            if (col == 8) {
                col = 0;
                row++;
            }
        }
    }
    
  
    

    
    private static VBox createLivreBox(Livre livre) {
        VBox livreBox = new VBox(10);
        livreBox.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        try {
            String imagePath = livre.getImage();
            InputStream is = new FileInputStream(Paths.get(imagePath).toFile());
            Image image = new Image(is);
            imageView.setImage(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(150);
            is.close();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + livre.getImage() + "\n" + e.getMessage());
        }

        Label titre = new Label(livre.getTitre());
        titre.setWrapText(true);
        titre.setMaxWidth(100);
        titre.setAlignment(Pos.CENTER);

        Label auteur = new Label(livre.getAuteur());
        auteur.setWrapText(true);
        auteur.setMaxWidth(100);
        auteur.setAlignment(Pos.CENTER);

        livreBox.getChildren().addAll(imageView, titre, auteur);
        return livreBox;
    }

    
    private static GridPane createCheckBoxGrid(List<CheckBox> checkBoxes, int columns) {
        GridPane grid = new GridPane();
        int col = 0;
        int row = 0;
        for (CheckBox checkBox : checkBoxes) {
            grid.add(checkBox, col, row);
            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
        return grid;
    }

    private static void filterLivres() {
        String searchText = rechercheField.getText().toLowerCase();

        boolean allGenresSelected = false;
        for (CheckBox checkBox : genreCheckBoxes) {
            if (checkBox.isSelected() && checkBox.getText().equals("Tous les genres")) {
                allGenresSelected = true;
                break;
            }
        }

        boolean allEditeursSelected = false;
        for (CheckBox checkBox : editeurCheckBoxes) {
            if (checkBox.isSelected() && checkBox.getText().equals("Tous les éditeurs")) {
                allEditeursSelected = true;
                break;
            }
        }

        if (allGenresSelected) {
            for (CheckBox checkBox : genreCheckBoxes) {
                if (!checkBox.getText().equals("Tous les genres")) {
                    checkBox.setSelected(false);
                }
            }
        }

        if (allEditeursSelected) {
            for (CheckBox checkBox : editeurCheckBoxes) {
                if (!checkBox.getText().equals("Tous les éditeurs")) {
                    checkBox.setSelected(false);
                }
            }
        }

        List<String> selectedGenres = new ArrayList<>();
        for (CheckBox checkBox : genreCheckBoxes) {
            if (checkBox.isSelected() && !checkBox.getText().equals("Tous les genres")) {
                selectedGenres.add(checkBox.getText());
            }
        }

        List<String> selectedEditeurs = new ArrayList<>();
        for (CheckBox checkBox : editeurCheckBoxes) {
            if (checkBox.isSelected() && !checkBox.getText().equals("Tous les éditeurs")) {
                selectedEditeurs.add(checkBox.getText());
            }
        }

        List<Livre> filteredLivres = new ArrayList<>();
        for (Livre livre : livres) {
            boolean genreMatch = selectedGenres.isEmpty() || selectedGenres.contains(livre.getGenre());
            boolean editeurMatch = selectedEditeurs.isEmpty() || selectedEditeurs.contains(livre.getEditeur());
            boolean searchMatch = livre.getTitre().toLowerCase().contains(searchText) ||
                    livre.getAuteur().toLowerCase().contains(searchText) ||
                    String.valueOf(livre.getAnneePublication()).contains(searchText) ||
                    livre.getIsbn().toLowerCase().contains(searchText);
            if (genreMatch && editeurMatch && searchMatch) {
                filteredLivres.add(livre);
            }
        }
        populateGrid(grid, filteredLivres);
    }
    private static void displayBonjourMessage(Stage primaryStage) {
        // Crée une nouvelle fenêtre pour afficher les titres et les images des livres empruntés
        panierStage = new Stage(); // Utiliser la variable de classe panierStage au lieu de déclarer une nouvelle variable locale
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Ajoute une étiquette pour indiquer qu'il s'agit du contenu du panier
        Label label = new Label("Contenu du panier :");
        layout.getChildren().add(label); // Ajout de l'étiquette au layout

        // Ajoute une HBox pour chaque livre dans le panier
        for (Livre livre : panierLivres) {
            HBox livreBox = new HBox(10);
            livreBox.setAlignment(Pos.CENTER_LEFT);

            // Crée une ImageView pour afficher l'image du livre
            ImageView imageView = new ImageView();
            try {
                String imagePath = livre.getImage();
                InputStream is = new FileInputStream(Paths.get(imagePath).toFile());
                Image image = new Image(is);
                imageView.setImage(image);
                imageView.setFitWidth(70); // Taille de l'image réduite
                imageView.setFitHeight(100);
                is.close();
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + livre.getImage() + "\n" + e.getMessage());
            }

            // Crée une étiquette pour afficher le titre du livre
            Label titreLabel = new Label(livre.getTitre());
            titreLabel.setAlignment(Pos.CENTER_LEFT);

            // Crée un bouton "X" pour retirer le livre du panier
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button removeButton = new Button("X");
            removeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-weight: bold; -fx-border-color: red; -fx-border-width: 2px;");
            removeButton.setOnAction(e -> {
                panierLivres.remove(livre);
                displaySuccessMessage(); // Affiche un message de succès après avoir retiré le livre du panier
                layout.getChildren().remove(livreBox); // Supprime la HBox du livre du layout du panier
            });

            // Ajouter l'image, le titre et le bouton "X" à la HBox
            livreBox.getChildren().addAll(imageView, titreLabel, spacer, removeButton);

            // Ajoute la HBox au layout
            layout.getChildren().add(livreBox);
        }

        Button validerButton = new Button("Valider Panier");
        validerButton.setStyle("-fx-background-color: #0070c0; -fx-text-fill: white;");
        validerButton.setOnAction(e -> {
            int idMembre = MainScreen.userId;

            // Valider le panier si l'utilisateur est connecté
            if (idMembre > 0) {
                DatabaseConnection.validerPanier(panierLivres, idMembre);
                System.out.println("Panier validé pour le membre ID: " + idMembre);

                // Fermer la fenêtre du panier après validation
                panierStage.close();

                panierLivres.clear();
            } else {
                // Afficher un message si l'utilisateur n'est pas connecté
                System.out.println("Utilisateur non connecté. Veuillez vous connecter pour valider le panier.");
            }
        });

        layout.getChildren().add(validerButton);

        // Création d'un ScrollPane pour permettre le défilement vertical
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true); // Redimensionne automatiquement la largeur du ScrollPane
        scrollPane.setFitToHeight(true); // Redimensionne automatiquement la hauteur du ScrollPane

        // Crée une scène avec le ScrollPane
        Scene scene = new Scene(scrollPane, 400, 400);

        // Configure la scène dans la fenêtre du panier et l'affiche
        panierStage.setScene(scene);
        panierStage.initModality(Modality.NONE); // Définit la fenêtre de panier en tant que fenêtre non modale
        panierStage.initOwner(primaryStage); // Supprime la fenêtre parente pour éviter les problèmes d'affichage des alertes

        panierStage.show();
    }



    private static void displaySuccessMessage() {
        // Crée une nouvelle fenêtre pour afficher le message de succès
        Stage successStage = new Stage();
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Ajoute un label pour le message de succès
        Label successLabel = new Label("Le livre a été retiré du panier avec succès !");
        successLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        // Ajoute un bouton pour fermer la fenêtre de succès
        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> successStage.close());

        layout.getChildren().addAll(successLabel, closeButton);

        // Crée une scène avec le layout
        Scene scene = new Scene(layout);

        // Configure la scène dans la fenêtre du succès et l'affiche
        successStage.setScene(scene);
        successStage.initModality(Modality.WINDOW_MODAL); // La fenêtre modale bloque uniquement la fenêtre parente
        successStage.initOwner(panierStage); // Définit la fenêtre parente à la fenêtre du panier
        successStage.show();
    }




    private static void showBookDetails(Livre livre) {
        Stage primaryStage = (Stage) rechercheField.getScene().getWindow();
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Détails du livre");

        // Rend la fenêtre principale non modifiable tant que la fenêtre de détails du livre est ouverte
        detailsStage.initOwner(primaryStage);
        detailsStage.initModality(Modality.WINDOW_MODAL);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER); // Centrer tous les éléments à l'intérieur de la VBox

        // Ajout de l'image du livre
        ImageView imageView = new ImageView();
        try {
            String imagePath = livre.getImage();
            InputStream is = new FileInputStream(Paths.get(imagePath).toFile());
            Image image = new Image(is);
            imageView.setImage(image);
            imageView.setFitWidth(300); // ajustez la largeur de l'image en conséquence
            imageView.setFitHeight(450); // ajustez la hauteur de l'image en conséquence
            is.close();
            layout.getChildren().add(imageView); // Ajoutez l'image au layout
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + livre.getImage() + "\n" + e.getMessage());
        }

        Map<Integer, Integer> niveauxPenalites = DatabaseConnection.recupererNiveauPenalitesPourUtilisateursConnectes(MainScreen.userId);

        Integer niveauPenalite = niveauxPenalites.get(MainScreen.userId);

        // Vérifier si le niveau de pénalité n'est pas null
        Button emprunterButton = new Button("Emprunter");
        emprunterButton.setStyle("-fx-background-color: #0070c0; -fx-text-fill: white;"); // Style bleu avec texte blanc
        emprunterButton.setOnAction(e -> {
            // Ajoute le titre du livre dans le panier
            ajouterAuPanier(livre);
            // Affiche le contenu du panier (peut être supprimé si non nécessaire)
            afficherPanier();
            // Ferme la fenêtre de détails du livre après avoir ajouté au panier
            detailsStage.close();
        });

        // Si le niveau de pénalité n'est pas null, désactiver le bouton emprunter
        if (niveauPenalite != null) {
            emprunterButton.setDisable(true);
        }

    

        // Vérifier si la liste d'avis n'est pas vide
        Button avisButton = new Button("Avis");
        List<Avis> avisList = DatabaseConnection.getAvisPourLivre(livre.getId());
        List<Membre> membresList = DatabaseConnection.getMembres(); // Remplacez cette méthode par celle qui récupère tous les membres depuis la base de données
        if (!avisList.isEmpty()) {
            // Si la liste d'avis n'est pas vide, afficher le bouton
            avisButton.setOnAction(e -> {
                displayAvis(detailsStage, avisList, membresList); // Passer le detailsStage et la liste des membres comme paramètres
            });
        } else {
            // Si la liste d'avis est vide, désactiver le bouton
            avisButton.setDisable(true);
        }


        layout.getChildren().addAll(
            new Label("Titre: " + livre.getTitre()),
            new Label("Auteur: " + livre.getAuteur()),
            new Label("Genre: " + livre.getGenre()),
            new Label("Éditeur: " + livre.getEditeur()),
            new Label("ISBN: " + livre.getIsbn()),
            new Label("Année de publication: " + livre.getAnneePublication()),
            new Label("Stock: " + livre.getQuantiteDisponible()),
            emprunterButton,
            avisButton
        );

        Scene scene = new Scene(layout, 600, 730);
        detailsStage.setScene(scene);
        detailsStage.showAndWait(); // Attend que la fenêtre soit fermée avant de revenir à la fenêtre principale
    }


    
    private static void displayAvis(Stage stage, List<Avis> avisList, List<Membre> membresList) {
        VBox avisDisplay = new VBox(10); // VBox avec un espacement de 10 entre les éléments
        avisDisplay.setAlignment(Pos.CENTER);
        
        for (Avis avis : avisList) {
            VBox avisBox = new VBox(5); // VBox pour chaque avis avec un espacement de 5
            avisBox.setAlignment(Pos.CENTER_LEFT);
            avisBox.setStyle("-fx-padding: 10; -fx-border-style: solid inside;"
                            + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
                            + "-fx-border-radius: 5;" + "-fx-border-color: grey;");
        
            // Recherche du membre correspondant à l'ID membre de l'avis
            Membre membre = findMembreById(membresList, avis.getIdMembre());
            String nomPrenom = membre.getPrenom() + " " + membre.getNom(); // Concaténation du nom et du prénom
            
            Label membreLabel = new Label("Membre : " + nomPrenom); // Affichage du nom et prénom
            Label noteLabel = new Label("Note : " + avis.getNote());
            Label commentaireLabel = new Label("Commentaire : " + avis.getCommentaire());
            commentaireLabel.setWrapText(true);
        
            avisBox.getChildren().addAll(membreLabel, noteLabel, commentaireLabel);
            avisDisplay.getChildren().add(avisBox);
            
            Separator separator = new Separator();
            separator.setPadding(new Insets(10, 0, 10, 0)); // Ajouter de l'espace autour du séparateur
            avisDisplay.getChildren().add(separator);
        }
        
        // Afficher les avis dans une nouvelle fenêtre ou dialogue
        Stage avisStage = new Stage();
        avisStage.initModality(Modality.WINDOW_MODAL);
        avisStage.initOwner(stage);
        Scene avisScene = new Scene(avisDisplay, 300, 600);
        avisStage.setScene(avisScene);
        avisStage.setTitle("Avis sur le livre");
        avisStage.showAndWait();
    }

    // Méthode utilitaire pour trouver un membre par son ID
    private static Membre findMembreById(List<Membre> membresList, int id) {
        for (Membre membre : membresList) {
            if (membre.getId() == id) {
                return membre;
            }
        }
        return null;
    }


    private static VBox LivreRecommande(Stage primaryStage) {

        // Création d'une VBox pour contenir les livres recommandés
        VBox livresRecommandesBox = new VBox(10);
        livresRecommandesBox.setAlignment(Pos.CENTER);

        // Supposons que vous ayez une méthode pour obtenir l'ID du membre connecté
        int idMembreConnecte = MainScreen.userId; // Remplacez cela par votre méthode pour obtenir l'ID du membre connecté

        // Récupérez les livres recommandés pour le membre connecté par genre
        List<Livre> livresRecommandes = DatabaseConnection.getRecommendedBooks(idMembreConnecte);

        // Ajoutez des gestionnaires d'événements aux livres recommandés par genre
        for (Livre livre : livresRecommandes) {
            VBox livreBox = createLivreBox(livre);
            livreBox.setOnMouseClicked(event -> showBookDetails(livre)); // Ajoute le gestionnaire d'événements
            livresRecommandesBox.getChildren().add(livreBox);
        }

        // Récupérez les livres recommandés pour le membre connecté par auteur
        List<Livre> livresRecommandesByAuthor = DatabaseConnection.getRecommendedBooksByAuthor(idMembreConnecte);

        // Ajoutez des gestionnaires d'événements aux livres recommandés par auteur
        for (Livre livre : livresRecommandesByAuthor) {
            VBox livreBox = createLivreBox(livre);
            livreBox.setOnMouseClicked(event -> showBookDetails(livre)); // Ajoute le gestionnaire d'événements
            livresRecommandesBox.getChildren().add(livreBox);
        }

        // Encapsulez la VBox dans un ScrollPane
        ScrollPane scrollPane = new ScrollPane(livresRecommandesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Spécifiez explicitement la largeur du ScrollPane
        scrollPane.setPrefViewportWidth(300); // Modifier la valeur 300 selon vos besoins

        return new VBox(scrollPane); // Retourne une VBox contenant le ScrollPane
    }

}

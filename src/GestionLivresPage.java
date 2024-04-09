import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionLivresPage extends Application {

    private TableView<Livre> table1 = new TableView<>();
    private TableView<Livre> table2 = new TableView<>();
    private ObservableList<Livre> table1Data = FXCollections.observableArrayList();
    private ObservableList<Livre> table2Data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
    
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        
        
        Button retourButton = new Button("Retour");
        retourButton.setOnAction(event -> primaryStage.close()); 
        
        
        HBox retourBox = new HBox(retourButton);
        
        Button ajoutLivreButton = new Button("+ Ajouter Livre");
        

        ajoutLivreButton.setOnAction(event -> ajoutButtonClicked());

        Button sendButton = new Button("Retirer");

        // Set up table columns for	 table1
        TableColumn<Livre, String> idColumn1 = new TableColumn<>("ID");
        idColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getId())));
        TableColumn<Livre, String> titleColumn1 = new TableColumn<>("Titre");
        titleColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        titleColumn1.setPrefWidth(280);
        TableColumn<Livre, String> authorColumn1 = new TableColumn<>("Auteur");
        authorColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuteur()));
        authorColumn1.setPrefWidth(240);
        TableColumn<Livre, String> yearColumn1 = new TableColumn<>("Année de publication");
        yearColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getAnneePublication())));
        yearColumn1.setPrefWidth(200);
        TableColumn<Livre, String> genreColumn1 = new TableColumn<>("Genre");
        genreColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenre()));
        genreColumn1.setPrefWidth(150);
        TableColumn<Livre, String> isbnColumn1 = new TableColumn<>("ISBN");
        isbnColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        isbnColumn1.setPrefWidth(150);
        TableColumn<Livre, String> editorColumn1 = new TableColumn<>("Editeur");
        editorColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEditeur()));
        editorColumn1.setPrefWidth(150);
        TableColumn<Livre, String> quantityColumn1 = new TableColumn<>("Stock");
        quantityColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getQuantiteDisponible())));
        quantityColumn1.setPrefWidth(50);

        Button sendButton1 = new Button("Envoyer");

        // Set up table columns for table2
        TableColumn<Livre, String> idColumn2 = new TableColumn<>("ID");
        idColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getId())));
        TableColumn<Livre, String> titleColumn2 = new TableColumn<>("Titre");
        titleColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        titleColumn2.setPrefWidth(280);
        TableColumn<Livre, String> authorColumn2 = new TableColumn<>("Auteur");
        authorColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuteur()));
        authorColumn2.setPrefWidth(240);
        TableColumn<Livre, String> yearColumn2 = new TableColumn<>("Année de publication");
        yearColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getAnneePublication())));
        yearColumn2.setPrefWidth(200);
        TableColumn<Livre, String> genreColumn2 = new TableColumn<>("Genre");
        genreColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenre()));
        genreColumn2.setPrefWidth(150);
        TableColumn<Livre, String> isbnColumn2 = new TableColumn<>("ISBN");
        isbnColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        isbnColumn2.setPrefWidth(150);
        TableColumn<Livre, String> editorColumn2 = new TableColumn<>("Editeur");
        editorColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEditeur()));
        editorColumn2.setPrefWidth(150);
        TableColumn<Livre, String> quantityColumn2 = new TableColumn<>("Stock");
        quantityColumn2.setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getQuantiteDisponible())));
        quantityColumn2.setPrefWidth(50);

        // Add columns to tables
        table1.getColumns().addAll(idColumn1, titleColumn1, authorColumn1, yearColumn1, genreColumn1, isbnColumn1, editorColumn1, quantityColumn1);
        table2.getColumns().addAll(idColumn2, titleColumn2, authorColumn2, yearColumn2, genreColumn2, isbnColumn2, editorColumn2, quantityColumn2);

        // Set data to tables
        table1.setItems(table1Data);
        table2.setItems(table2Data);

        table1.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table2.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // Allow multiple selection

        sendButton.setOnAction(event -> removeButtonClicked());

        HBox buttonBox = new HBox(sendButton);
        buttonBox.setSpacing(10);

        sendButton1.setOnAction(event -> sendButtonClicked());

        HBox buttonBox1 = new HBox(sendButton1);
        buttonBox1.setSpacing(40);

        HBox ajout = new HBox(ajoutLivreButton);

        Label titreTable1 = new Label("Livres Disponibles");
        titreTable1.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center;");

     
        Label titreTable2 = new Label("Livres base de donnée");
        titreTable2.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center;");
      
        
        root.getChildren().addAll(retourBox, ajout, titreTable1, table1,buttonBox, titreTable2, table2, buttonBox1);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Livres");
        primaryStage.setMaximized(true); // Maximiser la fenêtre
        primaryStage.show();

        // Populate table1 and table2
        populateTables();
    }

    private void populateTables() {
        // Effacer les données existantes des tables
        table1Data.clear();
        table2Data.clear();

        List<Livre> livres = DatabaseConnection.getLivres2();

        // Créer une HashMap pour stocker le nombre d'emprunts pour chaque livre
        Map<Integer, Integer> nombreEmpruntsMap = DatabaseConnection.getNombreEmpruntsParLivre();

        for (Livre livre : livres) {
            if (livre.getStatus()) {
                table1Data.add(livre);
            } else {
                table2Data.add(livre);
            }
        }

        // Ajoutez une colonne "Action" avec un bouton "Supprimer" à chaque table
        addButtonColumn(table1);
        addButtonColumn(table2);

        // Définir la colonne pour afficher le nombre d'emprunts
        TableColumn<Livre, Integer> nombreEmpruntsColumn1 = new TableColumn<>("Nbre d'emprunt");
        nombreEmpruntsColumn1.setCellValueFactory(cellData -> {
            int idLivre = cellData.getValue().getId();
            Integer nombreEmprunts = nombreEmpruntsMap.getOrDefault(idLivre, 0);
            return new SimpleIntegerProperty(nombreEmprunts).asObject();
        });
        nombreEmpruntsColumn1.setPrefWidth(150);

        TableColumn<Livre, Integer> nombreEmpruntsColumn2 = new TableColumn<>("Nbre d'emprunt");
        nombreEmpruntsColumn2.setCellValueFactory(cellData -> {
            int idLivre = cellData.getValue().getId();
            Integer nombreEmprunts = nombreEmpruntsMap.getOrDefault(idLivre, 0);
            return new SimpleIntegerProperty(nombreEmprunts).asObject();
        });
        nombreEmpruntsColumn2.setPrefWidth(150);

        // Ajouter la colonne "Nbre d'emprunt" à table1
        table1.getColumns().add(nombreEmpruntsColumn1);

        // Ajouter la colonne "Nbre d'emprunt" à table2
        table2.getColumns().add(nombreEmpruntsColumn2);
    }

    private void addButtonColumn(TableView<Livre> table) {
        if (table.getColumns().stream().noneMatch(column -> column.getText().equals("Action"))) {
            TableColumn<Livre, Void> actionColumn = new TableColumn<>("Action");
            actionColumn.setCellFactory(param -> {
                final TableCell<Livre, Void> cell = new TableCell<>() {
                    private final Button deleteButton = new Button();
                    private final Button editButton = new Button();

                    {
                        // Chargement de l'image de suppression
                        Image deleteImage = new Image(getClass().getResourceAsStream("bin.png"));
                        ImageView deleteImageView = new ImageView(deleteImage);
                        deleteImageView.setFitWidth(20);
                        deleteImageView.setFitHeight(20);
                        deleteButton.setGraphic(deleteImageView);

                        // Chargement de l'image d'édition
                        Image editImage = new Image(getClass().getResourceAsStream("editing.png"));
                        ImageView editImageView = new ImageView(editImage);
                        editImageView.setFitWidth(20);
                        editImageView.setFitHeight(20);
                        editButton.setGraphic(editImageView);

                        deleteButton.setOnAction(event -> {
                            Livre livre = getTableView().getItems().get(getIndex());
                            DatabaseConnection.deleteLivre(livre.getId());
                            populateTables();
                        });

                        editButton.setOnAction(event -> {
                            Livre livre = getTableView().getItems().get(getIndex());
                            openEditForm(livre);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(deleteButton, editButton);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
                return cell;
            });

            table.getColumns().add(actionColumn);
        }
    }
    
    private void openEditForm(Livre livre) {
        Stage editStage = new Stage();
        editStage.setTitle("Modifier Livre");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField titreField = new TextField(livre.getTitre());
        TextField auteurField = new TextField(livre.getAuteur());
        TextField anneeField = new TextField(Integer.toString(livre.getAnneePublication()));
        TextField genreField = new TextField(livre.getGenre());
        TextField isbnField = new TextField(livre.getIsbn());
        TextField editeurField = new TextField(livre.getEditeur());
        TextField quantiteField = new TextField(Integer.toString(livre.getQuantiteDisponible()));

        gridPane.add(new Label("Titre:"), 0, 0);
        gridPane.add(titreField, 1, 0);
        gridPane.add(new Label("Auteur:"), 0, 1);
        gridPane.add(auteurField, 1, 1);
        gridPane.add(new Label("Année de publication:"), 0, 2);
        gridPane.add(anneeField, 1, 2);
        gridPane.add(new Label("Genre:"), 0, 3);
        gridPane.add(genreField, 1, 3);
        gridPane.add(new Label("ISBN:"), 0, 4);
        gridPane.add(isbnField, 1, 4);
        gridPane.add(new Label("Éditeur:"), 0, 5);
        gridPane.add(editeurField, 1, 5);
        gridPane.add(new Label("Quantité disponible:"), 0, 6);
        gridPane.add(quantiteField, 1, 6);

        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(event -> {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            int anneePublication = Integer.parseInt(anneeField.getText());
            String genre = genreField.getText();
            String isbn = isbnField.getText();
            String editeur = editeurField.getText();
            int quantiteDisponible = Integer.parseInt(quantiteField.getText());

            DatabaseConnection.updateLivre(livre.getId(), titre, auteur, anneePublication, genre, isbn, editeur, quantiteDisponible, true);

            // Rafraîchir la table après la mise à jour du livre
            populateTables();

            // Fermer la fenêtre de modification
            editStage.close();

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Modification réussie");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Les détails du livre ont été modifiés avec succès.");
            successAlert.showAndWait();
        });

        gridPane.add(saveButton, 1, 7);

        Scene editScene = new Scene(gridPane, 400, 300);
        editStage.setScene(editScene);
        editStage.show();
    }


    private void removeButtonClicked() {
        ObservableList<Livre> selectedBooks = FXCollections.observableArrayList(table1.getSelectionModel().getSelectedItems()); // Crée une copie de la liste des livres sélectionnés
        for (Livre selectedBook : selectedBooks) {
            selectedBook.setStatus(false); // Mettre à jour le statut à false
            // Mettre à jour le statut dans la base de données
            DatabaseConnection.updateLivreStatus(selectedBook.getId(), false);
        }
        // Supprimer tous les livres sélectionnés de table1Data
        table1Data.removeAll(selectedBooks);
        // Ajouter tous les livres sélectionnés à table2Data
        table2Data.addAll(selectedBooks);
        table2Data.sort(Comparator.comparingInt(Livre::getId));
    }

    private void sendButtonClicked() {
        ObservableList<Livre> selectedBooks1 = FXCollections.observableArrayList(table2.getSelectionModel().getSelectedItems()); // Crée une copie de la liste des livres sélectionnés
        for (Livre selectedBook1 : selectedBooks1) {
            selectedBook1.setStatus(true);
            DatabaseConnection.updateLivreStatus(selectedBook1.getId(), true);
        }

        table2Data.removeAll(selectedBooks1);
        table1Data.addAll(selectedBooks1);
        table1Data.sort(Comparator.comparingInt(Livre::getId));
    }

    private String copyImageToImagesFolder(String sourceImagePath, int bookId) throws IOException {
        // Chemin de destination dans le dossier "images" du projet
        String destinationFolder = "images";

        // Créer le dossier "images" s'il n'existe pas
        File imagesFolder = new File(destinationFolder);
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }

     
        // Créer un nouveau nom de fichier en utilisant l'ID du livre
        String destinationFileName = bookId + ".png";


        // Chemin de destination complet
        String destinationImagePath = destinationFolder + File.separator + destinationFileName;

        // Copier le fichier source dans le dossier "images"
        Path source = new File(sourceImagePath).toPath();
        Path destination = new File(destinationImagePath).toPath();
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

        return destinationImagePath;
    }

    

    private void ajoutButtonClicked() {
        Stage ajoutStage = new Stage();
        ajoutStage.initModality(Modality.APPLICATION_MODAL);
        ajoutStage.setTitle("Ajouter un livre");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Créer les champs de formulaire pour saisir les détails du livre
        TextField titreField = new TextField();
        TextField auteurField = new TextField();
        TextField anneeField = new TextField();
        TextField genreField = new TextField();
        TextField isbnField = new TextField();
        TextField editeurField = new TextField();
        TextField quantiteField = new TextField("5");

        // Ajouter un champ pour le chemin de l'image
        TextField imagesField = new TextField();
        imagesField.setEditable(false); // Empêcher l'utilisateur de modifier le chemin manuellement

        Button choisirImageButton = new Button("Choisir une image");
        choisirImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Image", "*.png", "*.jpg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(ajoutStage);
            if (selectedFile != null) {
                // Obtenir le chemin absolu de l'image sélectionnée
                String imagePath = selectedFile.getAbsolutePath();
                // Afficher le chemin de l'image dans le champ texte
                imagesField.setText(imagePath);
            }
        });

        Button ajouterButton = new Button("Ajouter");

        ajouterButton.setOnAction(e -> {
            // Récupérer les valeurs saisies dans le formulaire
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            int anneePublication = Integer.parseInt(anneeField.getText());
            String genre = genreField.getText();
            String isbn = isbnField.getText();
            String editeur = editeurField.getText();
            int quantite = Integer.parseInt(quantiteField.getText());

            String imagePath = imagesField.getText();

            // Insérer le livre dans la base de données et récupérer son ID
            int newBookId = DatabaseConnection.insertLivre(titre, auteur, anneePublication, genre, isbn, editeur, quantite, false);

            // Copier l'image vers le dossier "images" en utilisant l'ID du livre
            try {
                String destinationImagePath = copyImageToImagesFolder(imagePath, newBookId);
                // Mettre à jour le champ de texte pour le chemin de l'image
                imagesField.setText(destinationImagePath); // Mettre à jour le champ de texte avec le chemin de l'image
                // Afficher le chemin de l'image après la copie
                System.out.println("Chemin de l'image enregistrée : " + destinationImagePath);
            } catch (IOException ex) {
                ex.printStackTrace();
                // Gérer l'erreur de copie de l'image
            }

            // Fermer la fenêtre de formulaire après l'ajout du livre
            ajoutStage.close();

            // Mettre à jour les tables
            populateTables();
        });

        // Ajouter les champs et les boutons au GridPane
        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Auteur:"), 0, 1);
        grid.add(auteurField, 1, 1);
        grid.add(new Label("Année de publication:"), 0, 2);
        grid.add(anneeField, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("ISBN:"), 0, 4);
        grid.add(isbnField, 1, 4);
        grid.add(new Label("Editeur:"), 0, 5);
        grid.add(editeurField, 1, 5);
      
        grid.add(new Label("Image:"), 0, 7);
        grid.add(imagesField, 1, 7);
        grid.add(choisirImageButton, 2, 7);
        grid.add(ajouterButton, 1, 8);

        Scene scene = new Scene(grid, 600, 400);
        ajoutStage.setScene(scene);
        ajoutStage.show();
        populateTables();
    }

    
   
}

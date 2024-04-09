import javafx.beans.property.SimpleStringProperty;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class MembrePage {

    public static Scene createMembrePageScene(Stage primaryStage, Scene mainScene, List<Membre> membres, Scene previousScene) {

        TableView<Membre> table = new TableView<>();

        TableColumn<Membre, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));

        TableColumn<Membre, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));

        TableColumn<Membre, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAdresse()));

        TableColumn<Membre, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<Membre, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));


        TableColumn<Membre, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(column -> new TableCell<Membre, Void>() {
            private final Button deleteButton = new Button();
            private final Button updateButton = new Button();


            {
                // Set the image on the delete button
                ImageView binIcon = new ImageView(new Image("bin.png"));
                binIcon.setFitWidth(16);
                binIcon.setFitHeight(16);
                deleteButton.setGraphic(binIcon);

                // Set the image on the update button
                ImageView editIcon = new ImageView(new Image("editing.png"));
                editIcon.setFitWidth(16);
                editIcon.setFitHeight(16);
                updateButton.setGraphic(editIcon);



                // Set action for the delete button
                deleteButton.setOnAction(event -> {
                    Membre membre = getTableView().getItems().get(getIndex());
                    // Appel à la méthode de suppression dans la base de données
                    DatabaseConnection.deleteMembre(membre.getId());
                    // Mise à jour du tableau en supprimant le membre
                    getTableView().getItems().remove(membre);
                });

                // Set action for the update button
                updateButton.setOnAction(event -> {
                    Membre membre = getTableView().getItems().get(getIndex());
                    showUpdateMembreDialog(membre, table); // Passer la référence de la TableView
                });


            }


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Add all buttons to the cell
                    HBox buttons = new HBox(5); // Change VBox to HBox
                    buttons.getChildren().addAll(deleteButton, updateButton);
                    buttons.setAlignment(Pos.CENTER); // Align horizontally
                    setGraphic(buttons);
                }
            }
        });
        Map<Integer, Integer> nombrePenalitesMap = DatabaseConnection.getNombrePenalitesParMembre();


        TableColumn<Membre, String> empruntColumn = new TableColumn<>("Emprunt");
        empruntColumn.setCellValueFactory(cellData -> {
            int idMembre = cellData.getValue().getId();
            int nombreLivresEmpruntes = DatabaseConnection.getNombreLivresEmpruntesParMembre().getOrDefault(idMembre, 0);
            return new SimpleStringProperty(Integer.toString(nombreLivresEmpruntes));
        });
        TableColumn<Membre, String> penaCol = new TableColumn<>("Pénalité");
        penaCol.setCellValueFactory(cellData -> {
            int idMembre = cellData.getValue().getId();
            int nombrePenalites = nombrePenalitesMap.getOrDefault(idMembre, 0);
            return new SimpleStringProperty(Integer.toString(nombrePenalites));
        });


        TableColumn<Membre, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cellData -> {
            int status = cellData.getValue().getStatus();
            String statusString = (status == 0) ? "Bloqué" : "Actif";
            return new SimpleStringProperty(statusString);
        });

        statutCol.setCellFactory(column -> new TableCell<Membre, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);


                    if (item.equals("Actif")) {
                        setStyle("-fx-font-weight: bold;  -fx-text-fill: green;");
                    } else {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
                    }
                }
            }
        });

        nomCol.setPrefWidth(100); 
        prenomCol.setPrefWidth(100); 
        adresseCol.setPrefWidth(150); 
        emailCol.setPrefWidth(200); 
        roleCol.setPrefWidth(100); 
        actionCol.setPrefWidth(100); 
        empruntColumn.setPrefWidth(80); 
        penaCol.setPrefWidth(60); 
        statutCol.setPrefWidth(100);
        
        table.getColumns().addAll(nomCol, prenomCol, adresseCol, emailCol, roleCol, actionCol, empruntColumn, penaCol, statutCol);
        for (Membre membre : membres) {
            if (!membre.getEmail().equals("admin@admin.com")) {
                table.getItems().add(membre);
            }
        }


        Button backButton = new Button("Retour");
        backButton.setOnAction(event -> {
            primaryStage.setScene(previousScene); // Utiliser la référence de la scène précédente
            primaryStage.setFullScreen(true); // Mode plein écran
        });
        VBox tableLayout = new VBox(10);
        tableLayout.getChildren().addAll(table, createBackButton(primaryStage,previousScene));
        tableLayout.setAlignment(Pos.CENTER);
        tableLayout.setStyle("-fx-padding: 10px;");
        tableLayout.setMaxWidth(1000); // Définir une largeur maximale pour la VBox
        
        // Création du titre
        Label titleLabel = new Label("Gestion des Membres");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(titleBox, tableLayout);
        mainLayout.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(mainLayout);

        return new Scene(root, 800, 600);
    }
    private static Button createBackButton(Stage primaryStage, Scene previousScene) {
        Button backButton = new Button("Retour");
        backButton.setOnAction(event -> {
            primaryStage.setScene(previousScene); // Utiliser la référence de la scène précédente
            primaryStage.setFullScreen(true); // Mode plein écran
        });
        return backButton;
    }

    private static void showUpdateMembreDialog(Membre membre, TableView<Membre> table) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(table.getScene().getWindow()); // Utilisation de la référence de la TableView
        dialogStage.setTitle("Modifier Membre");

        // Créer une liste déroulante (ComboBox) pour sélectionner le rôle
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("user", "admin"); // Ajouter les valeurs possibles
        roleComboBox.setValue(membre.getRole());
        ComboBox<Integer> statutComboBox = new ComboBox<>();
        statutComboBox.getItems().addAll(0, 1);
        statutComboBox.setValue(membre.getStatus());// Sélectionner la valeur actuelle


        TextField nomField = new TextField(membre.getNom());
        TextField prenomField = new TextField(membre.getPrenom());
        TextField adresseField = new TextField(membre.getAdresse());
        TextField emailField = new TextField(membre.getEmail());

        Button updateButton = new Button("Valider");
        updateButton.setStyle("-fx-background-color: #0077cc; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setOnAction(event -> {
          
            membre.setNom(nomField.getText());
            membre.setPrenom(prenomField.getText());
            membre.setAdresse(adresseField.getText());
            membre.setEmail(emailField.getText());
            membre.setRole(roleComboBox.getValue());
            membre.setStatus(statutComboBox.getValue());
            DatabaseConnection.updateMembre(membre);
        
            table.refresh();
            dialogStage.close();
        });

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
                new Label("Nom: "), nomField,
                new Label("Prénom: "), prenomField,
                new Label("Adresse: "), adresseField,
                new Label("Email: "), emailField,
                new Label("Rôle: "), roleComboBox,
                new Label("Statut: "), statutComboBox,
                updateButton
        );
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 10px;");

        Scene dialogScene = new Scene(vbox, 400, 500);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }


}

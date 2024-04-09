import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Date;
import java.util.List;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Pair;

public class createEmpruntsScene extends Application {

    private Scene mainScene;


	@Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Mes Emprunts");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.getChildren().add(titleLabel);

        TableView<EmpruntDetaille> empruntsTableView = new TableView<>();
        empruntsTableView.setItems(getEmpruntsDetaillesParMembre(MainScreen.userId));
        empruntsTableView.setMaxWidth(1335);

        TableColumn<EmpruntDetaille, String> titreColumn = new TableColumn<>("Titre");
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));

        TableColumn<EmpruntDetaille, String> auteurColumn = new TableColumn<>("Auteur");
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));

        TableColumn<EmpruntDetaille, String> editeurColumn = new TableColumn<>("Éditeur");
        editeurColumn.setCellValueFactory(new PropertyValueFactory<>("editeur"));

        TableColumn<EmpruntDetaille, Integer> anneeColumn = new TableColumn<>("Année de Publication");
        anneeColumn.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));

        TableColumn<EmpruntDetaille, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<EmpruntDetaille, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<EmpruntDetaille, Date> dateEmpruntColumn = new TableColumn<>("Date d'Emprunt");
        dateEmpruntColumn.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));

        TableColumn<EmpruntDetaille, Date> dateRetourPrevueColumn = new TableColumn<>("Date de Retour Prévue");
        dateRetourPrevueColumn.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        TableColumn<EmpruntDetaille, String> estRenduColumn = new TableColumn<>("Retourné");
        estRenduColumn.setCellValueFactory(cellData -> 
                cellData.getValue().isEstRendu() ? 
                new SimpleStringProperty("Oui") : 
                new SimpleStringProperty("Non"));

        estRenduColumn.setCellFactory(column -> new TableCell<EmpruntDetaille, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle(""); // Clear any previous styling
                } else {
                    setText(item);
                    if ("Oui".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        
        TableColumn<EmpruntDetaille, Void> actionColumn = new TableColumn<>("Avis");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button returnButton = new Button("Retourner");
            private final Button commentButton = new Button("Commentaire/Note");

            {
                returnButton.setOnAction(event -> {
                    EmpruntDetaille emprunt = getTableView().getItems().get(getIndex());
                    DatabaseConnection.retournerLivre(emprunt.getIdEmprunt());
                    returnButton.setDisable(true); 
                    loadData(); 
                });

                commentButton.setOnAction(event -> {
                    EmpruntDetaille emprunt = getTableView().getItems().get(getIndex());
                    showCommentAndRatingDialog(emprunt, primaryStage);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, returnButton, commentButton);
                    EmpruntDetaille emprunt = getTableView().getItems().get(getIndex());
                    returnButton.setDisable(emprunt.isEstRendu());
                    commentButton.setDisable(!emprunt.isEstRendu());
                    setGraphic(hbox);
                }
            }

            private void loadData() {
                empruntsTableView.setItems(getEmpruntsDetaillesParMembre(MainScreen.userId));
            }


        });


        
        

        empruntsTableView.getColumns().addAll(titreColumn, auteurColumn, editeurColumn, anneeColumn, genreColumn, isbnColumn, dateEmpruntColumn, dateRetourPrevueColumn, estRenduColumn, actionColumn);

        
        Button backButton = new Button("Retour");
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainScene); 
            primaryStage.setFullScreen(true);
        });

        root.getChildren().addAll(empruntsTableView, backButton);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Détails des Emprunts");

        // Afficher la scène en plein écran sans message ESC
        primaryStage.setFullScreen(true);

        primaryStage.show();

    }

    private ObservableList<EmpruntDetaille> getEmpruntsDetaillesParMembre(int idMembre) {
        List<EmpruntDetaille> emprunts = DatabaseConnection.getEmpruntsDetaillesParMembre(idMembre);
        return FXCollections.observableArrayList(emprunts);
    }


    private void showCommentAndRatingDialog(EmpruntDetaille emprunt, Stage primaryStage) {
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Commentaire et Note");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the comment and rating inputs.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Commentaire");
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 3);

        grid.add(new Label("Commentaire:"), 0, 0);
        grid.add(commentArea, 1, 0);
        grid.add(new Label("Note:"), 0, 1);
        grid.add(ratingSpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a comment-rating-pair when the OK button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(commentArea.getText(), ratingSpinner.getValue());
            }
            return null;
        });

        // Set the dialog's owner to the primary stage.
        dialog.initOwner(primaryStage);

        Optional<Pair<String, Integer>> result = dialog.showAndWait();

        result.ifPresent(commentRating -> {
            String comment = commentRating.getKey();
            int rating = commentRating.getValue();
            // Here you call your method to handle the response
            // For example, save the comment and rating to the database
            DatabaseConnection.ajouterCommentaire(emprunt.getIdEmprunt(), comment, rating);
        });
    }
    public static Scene createEmpruntScenes(Stage primaryStage, Scene mainScene) {
       createEmpruntsScene empruntPage = new createEmpruntsScene(); 
        empruntPage.setMainScene(mainScene); 
        empruntPage.start(primaryStage); 
        return primaryStage.getScene(); 
    }

  
    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }
}

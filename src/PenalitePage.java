	import java.sql.SQLException;
	import java.util.Date;
	import javafx.scene.image.Image;
	
	import java.util.List;
	import java.util.Optional;
	
	import javafx.application.Application;
	import javafx.collections.FXCollections;
	import javafx.collections.ObservableList;
	import javafx.geometry.Pos;
	import javafx.scene.Scene;
	import javafx.scene.control.TableColumn;
	import javafx.scene.control.TableRow;
	import javafx.scene.control.TableView;
	import javafx.scene.control.TextInputDialog;
	import javafx.scene.control.cell.PropertyValueFactory;
	import javafx.scene.image.ImageView;
	import javafx.scene.layout.HBox;
	import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
	import javafx.scene.control.Alert;
	import javafx.scene.control.Alert.AlertType;
	import javafx.scene.control.Button;
	import javafx.scene.control.ComboBox;
	import javafx.scene.control.Label;
	import javafx.scene.control.TableCell;
	
	public class PenalitePage extends Application {
	
	    private Scene mainScene; // Référence à la scène principale
	
	    @Override
	    public void start(Stage primaryStage) {
	        TableView<Penalite> tableView = new TableView<>();
	        tableView.setMaxWidth(670);
	        TableColumn<Penalite, Integer> idPenaliteColumn = new TableColumn<>("ID Penalite");
	        idPenaliteColumn.setCellValueFactory(new PropertyValueFactory<>("idPenalite"));
	        idPenaliteColumn.setPrefWidth(100);
	        TableColumn<Penalite, Integer> idMembreColumn = new TableColumn<>("ID Membre");
	        idMembreColumn.setCellValueFactory(new PropertyValueFactory<>("idMembre"));
	        idMembreColumn.setPrefWidth(100);
	
	        TableColumn<Penalite, String> raisonColumn = new TableColumn<>("Raison");
	        raisonColumn.setCellValueFactory(new PropertyValueFactory<>("raison"));
	        raisonColumn.setPrefWidth(200);
	
	        TableColumn<Penalite, Date> datePenaliteColumn = new TableColumn<>("Date Penalite");
	        datePenaliteColumn.setCellValueFactory(new PropertyValueFactory<>("datePenalite"));
	        datePenaliteColumn.setPrefWidth(100);
	        
	        TableColumn<Penalite, Integer> niveauPenaliteColumn = new TableColumn<>("Niveau");
	        niveauPenaliteColumn.setCellValueFactory(new PropertyValueFactory<>("niveauPenalite"));
	        niveauPenaliteColumn.setPrefWidth(100);
	        niveauPenaliteColumn.setCellFactory(column -> {
	            return new TableCell<Penalite, Integer>() {
	                protected void updateItem(Integer item, boolean empty) {
	                    super.updateItem(item, empty);
	                    TableRow<Penalite> row = getTableRow();
	                    if (row == null || empty) {
	                        setText(""); 
	                        setStyle(""); 
	                    } else {
	                        Penalite penalite = row.getItem();
	                        if (penalite != null) {
	                            // Créer une liste déroulante pour les niveaux de pénalité
	                            ComboBox<Integer> comboBox = new ComboBox<>();
	                            comboBox.getItems().addAll(1, 2, 3);
	                            comboBox.setValue(item); 
	
	                      
	                            comboBox.setOnAction(event -> {
	                                int nouveauNiveau = comboBox.getValue();
	                                try {
	                                    DatabaseConnection.mettreAJourNiveauPenalite(penalite.getIdMembre(), nouveauNiveau);
	                                    penalite.setNiveauPenalite(nouveauNiveau);
	                                    
	                                    // Afficher une boîte de dialogue de succès
	                                    afficherNotificationSucces("Niveau de pénalité mis à jour avec succès !", primaryStage, mainScene);

	                                } catch (SQLException e) {
	                                    e.printStackTrace();
	                                    
	                                }
	                            });
	
	
	                           
	                            setGraphic(comboBox);
	                            setText(null); 
	                        }
	                    }
	                }
	            };
	        });
	
	        TableColumn<Penalite, Void> actionCol = new TableColumn<>("Action");
	
	        // Définition de la cellFactory pour la colonne "Action"
	        actionCol.setCellFactory(param -> new TableCell<Penalite, Void>() {
	            private final Button deleteButton = new Button();
	
	            {
	                // Set the image on the delete button
	                ImageView binIcon = new ImageView(new Image("bin.png"));
	                binIcon.setFitWidth(16);
	                binIcon.setFitHeight(16);
	                deleteButton.setGraphic(binIcon);
	
	                // Set action for the delete button
	                deleteButton.setOnAction(event -> {
	                    Penalite penalite = getTableView().getItems().get(getIndex());
	                    // Appel à la méthode de suppression dans la base de données
	                    try {
	                        DatabaseConnection.supprimerPenalite(penalite.getIdPenalite());
	                    } catch (SQLException e) {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
	                    }
	                    // Mise à jour du tableau en supprimant le membre
	                    getTableView().getItems().remove(penalite);
	                });
	            }
	
	            @Override
	            protected void updateItem(Void item, boolean empty) {
	                super.updateItem(item, empty);
	                if (empty) {
	                    setGraphic(null);
	                } else {
	                    // Add delete button to the cell
	                    setGraphic(deleteButton);
	                }
	            }
	        });
	
	        tableView.getColumns().addAll(idPenaliteColumn, idMembreColumn, raisonColumn, datePenaliteColumn,niveauPenaliteColumn, actionCol);
	
	        // Charger les pénalités depuis la base de données
	        List<Penalite> penalites = DatabaseConnection.getPenalites();
	
	        // Convertir la liste en ObservableList pour la TableView
	        ObservableList<Penalite> observablePenalites = FXCollections.observableArrayList(penalites);
	        tableView.setItems(observablePenalites);
	
	      
	        
	        VBox root = new VBox(10); // Add some spacing between the widgets
	        root.setAlignment(Pos.CENTER); // Centrer le VBox sur la scène
	
	        // Add title "Pénalités" above the table
	        Label titleLabel = new Label("Pénalités");
	        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
	        root.getChildren().add(titleLabel);
	
	        root.getChildren().add(tableView);
	        Scene scene = new Scene(root, 600, 400);
	
	        primaryStage.setScene(scene);
	        primaryStage.setTitle("Liste des pénalités");
	        primaryStage.show();
	
	        Button backButton = new Button("Retour");
	        backButton.setOnAction(event -> {
	            primaryStage.setScene(mainScene); // Retour à la scène principale (AdminPage)
	            primaryStage.setFullScreen(true); // Mode plein écran
	        });
	
	        VBox layout = new VBox(10);
	        layout.getChildren().addAll(backButton); // Ajouter le bouton retour au layout
	        layout.setAlignment(Pos.CENTER);
	
	        root.getChildren().add(layout); // Ajouter le layout à la racine de la scène
	    }
	
	    private void afficherNotificationSucces(String message, Stage primaryStage, Scene mainScene) {
	        Stage successStage = new Stage();
	        successStage.initOwner(primaryStage);
	        successStage.initModality(Modality.APPLICATION_MODAL);

	        VBox root = new VBox(20);
	        root.setAlignment(Pos.CENTER);

	        Label successLabel = new Label(message);
	        Button okButton = new Button("OK");
	        okButton.setOnAction(event -> {
	            successStage.close();
	        });

	        root.getChildren().addAll(successLabel, okButton);

	        Scene successScene = new Scene(root, 300, 200);
	        successStage.setScene(successScene);
	        successStage.setTitle("Succès");
	        successStage.show();
	    }

	    // Méthode pour créer la scène de la page des pénalités en utilisant une référence à la scène principale
	    public static Scene createPenaliteScene(Stage primaryStage, Scene mainScene) {
	        PenalitePage penalitePage = new PenalitePage(); // Créer une instance de PenalitePage
	        penalitePage.setMainScene(mainScene); // Définir la référence à la scène principale
	        penalitePage.start(primaryStage); // Appeler la méthode start pour afficher la page des pénalités
	        return primaryStage.getScene(); // Retourner la scène de la page des pénalités
	    }
	
	    // Setter pour mainScene pour définir la référence depuis AdminPage
	    public void setMainScene(Scene mainScene) {
	        this.mainScene = mainScene;
	    }
	}

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javafx.scene.image.Image;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class EmpruntsTableApp extends Application {

    private Scene mainScene; // Référence à la scène principale

    @Override
    public void start(Stage primaryStage) {
        TableView<Emprunt> empruntTableView = new TableView<>();
        empruntTableView.setMaxWidth(700);
      
        TableColumn<Emprunt, Integer> idEmpruntColumn = new TableColumn<>("ID Emprunt");
        idEmpruntColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdEmprunt()).asObject());
        idEmpruntColumn.setPrefWidth(100);

        TableColumn<Emprunt, Integer> idLivreEmpruntColumn = new TableColumn<>("ID Livre");
        idLivreEmpruntColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdLivre()).asObject());
        idLivreEmpruntColumn.setPrefWidth(150);
        TableColumn<Emprunt, Integer> idMembreEmpruntColumn = new TableColumn<>("ID Membre");
        idMembreEmpruntColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdMembre()).asObject());
        idMembreEmpruntColumn.setPrefWidth(150);
        TableColumn<Emprunt, Date> dateEmpruntColumn = new TableColumn<>("Date Emprunt");
        dateEmpruntColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateEmprunt()));
        dateEmpruntColumn.setPrefWidth(300);
        
        empruntTableView.getColumns().addAll(idEmpruntColumn, idLivreEmpruntColumn, idMembreEmpruntColumn, dateEmpruntColumn);

        // Tableau des retours
        TableView<Retour> retoursTableView = new TableView<>();
        retoursTableView.setMaxWidth(700);
        
        TableColumn<Retour, Integer> idRetourColumn = new TableColumn<>("ID Retour");
        idRetourColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdRetour()).asObject());
        idRetourColumn.setPrefWidth(100);
        TableColumn<Retour, Integer> idEmpruntRetourColumn = new TableColumn<>("ID Emprunt");
        idEmpruntRetourColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getIdEmprunt()).asObject());
        idEmpruntRetourColumn.setPrefWidth(150);
        TableColumn<Retour, Date> dateRetourReelleColumn = new TableColumn<>("Date Retour Réelle");
        dateRetourReelleColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateRetourReelle()));
        dateRetourReelleColumn.setPrefWidth(150);
        dateRetourReelleColumn.setCellFactory(column -> {
            return new TableCell<Retour, Date>() {
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    TableRow<Retour> row = getTableRow();
                    if (row == null || empty) {
                        setText(""); 
                        setStyle(""); 
                    } else {
                        Retour retour = row.getItem();
                        if (retour != null && retour.getDateRetourReelle() == null && retour.getDateRetourPrevue().compareTo(new Date()) > 0) {
                            setText("Non retourné");
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); 
                        } else if (retour != null && retour.getDateRetourReelle() == null && retour.getDateRetourPrevue().compareTo(new Date()) <= 0) {
                            setText("Non retourné");
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else {
                            setText(item == null ? "" : item.toString()); 
                            setStyle(""); 
                        }
                    }
                }
            };
        });

        

        TableColumn<Retour, Date> dateRetourPrevueColumn = new TableColumn<>("Date Retour Prévue");
        dateRetourPrevueColumn.setPrefWidth(350);
        dateRetourPrevueColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateRetourPrevue()));
        dateRetourPrevueColumn.setCellFactory(column -> {
            return new TableCell<Retour, Date>() {
                private final HBox hbox = new HBox();
                private final Label dateLabel = new Label();
                private final Button calendarButton = new Button("Changer Date");

                {
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    dateLabel.setStyle("-fx-padding: 0 5px 0 0;");
                    calendarButton.setOnAction(event -> {
                        LocalDate localDate;
                        Date item = getItem();
                        if (item instanceof java.sql.Date) {
                            java.sql.Date sqlDate = (java.sql.Date) item;
                            localDate = sqlDate.toLocalDate();
                        } else {
                            localDate = item.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        }
                        DatePicker datePicker = new DatePicker(localDate);
                        datePicker.setPromptText("Select Date");
                        datePicker.setOnAction(dateEvent -> {
                            LocalDate selectedDate = datePicker.getValue();
                            int selectedIndex = getTableRow().getIndex();
                            Retour selectedRetour = retoursTableView.getItems().get(selectedIndex);
                            selectedRetour.setDateRetourPrevue(Date.from(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            DatabaseConnection.updateRetourDateRetourPrevue(selectedRetour.getIdRetour(), selectedRetour.getDateRetourPrevue());
                            calendarButton.setText("Calendar");
                            hbox.getChildren().remove(datePicker);
                            hbox.getChildren().add(calendarButton);
                            // Refresh the page to display the new date
                            retoursTableView.refresh();
                        });
                        hbox.getChildren().remove(calendarButton);
                        hbox.getChildren().add(datePicker);
                    });
                    hbox.getChildren().addAll(dateLabel, calendarButton);
                }

                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        dateLabel.setText(item.toString());
                        setText(null);
                        setGraphic(hbox);
                        // Convertir la date au format de la base de données
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = dateFormat.format(item);
                        dateLabel.setText(formattedDate);
                    }
                }
            };
        });


        retoursTableView.getColumns().addAll(idRetourColumn, idEmpruntRetourColumn, dateRetourReelleColumn, dateRetourPrevueColumn);


     

      
     // Charger les pénalités depuis la base de données
        List<Emprunt> emprunts = DatabaseConnection.getEmprunts();
        List<Retour> retours = DatabaseConnection.getRetours();

        // Convertir les listes en ObservableList pour la TableView
        ObservableList<Emprunt> observableEmprunt = FXCollections.observableArrayList(emprunts);
        ObservableList<Retour> observableRetour = FXCollections.observableArrayList(retours);

        // Ajouter les deux listes à la TableView
        empruntTableView.setItems(observableEmprunt);
        retoursTableView.setItems(observableRetour); // Ajoute également les retours dans la TableView

      
        
        VBox root = new VBox(10); // Add some spacing between the widgets
        root.setAlignment(Pos.CENTER); // Centrer le VBox sur la scène

        // Add title "Pénalités" above the table
        Label titleLabel = new Label("Emprunt");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.getChildren().add(titleLabel);

        root.getChildren().add(empruntTableView);
        Scene scene = new Scene(root, 600, 400);

        Label titleLabe2 = new Label("Retour");
        titleLabe2.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.getChildren().add(titleLabe2);

        root.getChildren().add(retoursTableView);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Liste des pénalités");
        primaryStage.show();

        Button backButton = new Button("Retour");
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainScene); 
            primaryStage.setFullScreen(true);
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(backButton); 
        layout.setAlignment(Pos.CENTER);

        root.getChildren().add(layout); 
    }
   

  

    public static Scene createEmpruntScene(Stage primaryStage, Scene mainScene) {
        EmpruntsTableApp empruntPage = new EmpruntsTableApp(); 
        empruntPage.setMainScene(mainScene); 
        empruntPage.start(primaryStage); 
        return primaryStage.getScene(); 
    }

  
    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }
}

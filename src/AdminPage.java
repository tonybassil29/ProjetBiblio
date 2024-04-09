import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class AdminPage {

    public static Scene createAdminPageScene(Stage primaryStage, Scene mainScene) {
        // Créer le contenu de la page d'administration
        Button manageMembersButton = createStyledButton("Gérer les membres", "button-green");
        manageMembersButton.setOnAction(event -> {
            List<Membre> membres = DatabaseConnection.getMembres(); 
            Scene membresPageScene = MembrePage.createMembrePageScene(primaryStage, mainScene, membres, primaryStage.getScene()); 
            primaryStage.setScene(membresPageScene);
            primaryStage.setFullScreen(true); 
        });

        Button manageBooksButton = createStyledButton("Gérer les livres", "button-blue");
        manageBooksButton.setOnAction(event -> {
            // Create a new instance of BookManagementApp
            GestionLivresPage gestionlivre = new GestionLivresPage();
            
            // Start the BookManagementApp window
            Stage stage = new Stage();
            gestionlivre.start(stage);
        });
        
        Button manageLoansButton = createStyledButton("Gérer les emprunts/retours", "button-yellow");
        manageLoansButton.setOnAction(event -> {
        	 Scene empruntScene = EmpruntsTableApp.createEmpruntScene(primaryStage, primaryStage.getScene());
             primaryStage.setScene(empruntScene);
             primaryStage.setFullScreen(true); 
        });


        Button managePenaltyButton = createStyledButton("Gérer les pénalités", "button-red");
        managePenaltyButton.setOnAction(event -> {  
            // Pass the main scene reference to PenalitePage
            Scene penaliteScene = PenalitePage.createPenaliteScene(primaryStage, primaryStage.getScene());
            primaryStage.setScene(penaliteScene);
            primaryStage.setFullScreen(true); // Mode plein écran
        });


        Button backButton = createStyledButton("Retour", "button-gray");
        backButton.setOnAction(event -> {
            primaryStage.setScene(mainScene); // Retour à la scène principale
            primaryStage.setFullScreen(true); // Mode plein écran
        });

        VBox adminLayout = new VBox(10);
        adminLayout.getStyleClass().add("vbox-container");
        adminLayout.getChildren().addAll(manageMembersButton, manageBooksButton, manageLoansButton, managePenaltyButton, backButton);
        adminLayout.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(adminLayout);

        // Appliquer la feuille de style CSS à la scène
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("admin.css");

        return scene;
    }

    // Fonction pour créer un bouton avec une classe CSS spécifique
    private static Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }
}

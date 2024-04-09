import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainScreen extends Application {
    
    private Scene mainScene;
    public static int userId;

    @Override
    public void start(Stage primaryStage) {
        // Configuration des éléments de la scène principale
        VBox centerContent = new VBox(10);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(20));
        Label welcomeLabel = new Label("Bienvenue à la Bibliothèque !");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        Label descriptionLabel = new Label("Votre portail de connaissances et de découvertes littéraires.");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        Label shortPresentation = new Label("Depuis 50 ans, avec plus de 10 000 livres et documents à votre disposition.");
        shortPresentation.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        centerContent.getChildren().addAll(welcomeLabel, descriptionLabel, shortPresentation);

        Button loginButton = new Button("Connexion");
        loginButton.setOnAction(event -> {
            primaryStage.setScene(showLoginForm(primaryStage));
            primaryStage.setFullScreen(true); // Assurez-vous que le Stage principal passe en mode plein écran
        });

        Button becomeMemberButton = new Button("Devenir membre");
        becomeMemberButton.setOnAction(event -> {
            primaryStage.setScene(showRegisterForm(primaryStage));
            primaryStage.setFullScreen(true); // Active le mode plein écran
        });

        HBox navBar = new HBox(10, loginButton, becomeMemberButton);
        navBar.setAlignment(Pos.CENTER_RIGHT);
        navBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(centerContent);
        root.setTop(navBar);

        // Configuration de la scène principale avec une image de fond
        Image backgroundImage = new Image("background.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(background));

        mainScene = new Scene(root, 800, 600);
        primaryStage.setTitle("Système de Gestion de Bibliothèque");
        primaryStage.setScene(mainScene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint(""); // Enlève le message d'échappement pour le mode plein écran
        primaryStage.show();
    }

    private Scene showRegisterForm(Stage primaryStage) {
        VBox registerLayout = new VBox(10);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.setPadding(new Insets(10));
        registerLayout.setMaxWidth(300);
        registerLayout.setMaxHeight(600);
        
        Label errorLabel = new Label(); 
        errorLabel.setTextFill(Color.RED);
     // Ajouter le label d'erreur au layout
        registerLayout.getChildren().add(errorLabel);

        Label nameLabel = new Label("Nom :");
        TextField nameField = new TextField();
        nameField.setPromptText("Entrez votre nom");
        
        Label prenomLabel = new Label("Prénom :");
        prenomLabel.getStyleClass().add("form-label"); 
        TextField prenomField = new TextField();
        prenomField.setPromptText("Entrez votre prénom");

        
        Label adresseLabel = new Label("Adresse :");
        adresseLabel.getStyleClass().add("form-label"); 
        TextField adresseField = new TextField();
        adresseField.setPromptText("Entrez votre adresse");

        Label emailLabel = new Label("Email :");
        TextField emailField = new TextField();
        emailField.setPromptText("Entrez votre email");

        Label passwordLabel = new Label("Mot de passe :");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");

        Label confirmPasswordLabel = new Label("Confirmer le mot de passe :");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmez votre mot de passe");

        Button registerButton = new Button("S'inscrire");
        registerButton.setOnAction(e -> {
        	 if (emailExists(emailField.getText())) {
        	        errorLabel.setText("Erreur: Email déjà utilisé."); 
        	    }
        	 else {
        		 if (insertUser(nameField.getText(), prenomField.getText(), adresseField.getText(), emailField.getText(), passwordField.getText()) && emailExists(emailField.getText())) {
                     System.out.println("Inscription réussie");
                     primaryStage.setScene(mainScene); 
                     primaryStage.setFullScreen(true);
                 } else {
                     System.out.println("Échec de l'inscription pas bien");
                   
                 }
        	 }
           
        });

        Button backButton = new Button("Retour");
        backButton.getStyleClass().add("form-button"); // Ajouter la classe CSS
        backButton.setOnAction(e -> {
            primaryStage.setScene(mainScene);
            primaryStage.setFullScreen(true);
        });

        // Apply some styling using CSS
        registerLayout.getStyleClass().add("form-container");
        nameLabel.getStyleClass().add("form-label");
        emailLabel.getStyleClass().add("form-label");
        passwordLabel.getStyleClass().add("form-label");
        confirmPasswordLabel.getStyleClass().add("form-label");
        registerButton.getStyleClass().add("form-button");
        registerLayout.getChildren().addAll(nameLabel, nameField, prenomLabel, prenomField, adresseLabel, adresseField, emailLabel, emailField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, registerButton, backButton);
        
        BorderPane outerContainer = new BorderPane();
        outerContainer.setCenter(registerLayout);

        Scene registerScene = new Scene(outerContainer, 600, 400);
        registerScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Link CSS file
        return registerScene;
    }

    private boolean insertUser(String nom, String prenom, String adresse, String email, String password) {
     

        String hashedPassword = hashPassword(password); // Hash du mot de passe

        String query = "INSERT INTO membre (nom, prenom, adresse, email, password, date_inscription, role, statut) VALUES (?, ?, ?, ?, ?, CURRENT_DATE(), 'user', '1')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, adresse);
            pstmt.setString(4, email);
            pstmt.setString(5, hashedPassword);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM membre WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    
    private AuthenticationResult authenticate(String email, String password) {
        String query = "SELECT id_membre, role, statut, password FROM membre WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            
            ResultSet resultSet = pstmt.executeQuery();
            
            if (resultSet.next()) {
                int userId = resultSet.getInt("id_membre");
                String role = resultSet.getString("role");
                int statut = resultSet.getInt("statut");
                String hashedPassword = resultSet.getString("password");
                
                String hashedInputPassword = hashPassword(password);
                
                if (hashedInputPassword != null && hashedInputPassword.equals(hashedPassword)) {
                    if (role.equals("user")) {
                        return new AuthenticationResult(true, UserRole.USER, userId, statut, null);
                    } else if (role.equals("admin")) {
                        return new AuthenticationResult(true, UserRole.ADMIN, userId, statut, null);
                    }
                }
            }
            return new AuthenticationResult(false, null, -1, -1, "Email ou mot de passe incorrect"); // Return error message
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new AuthenticationResult(false, null, -1, -1, "Erreur lors de l'authentification");
        }
    }
    private Scene showLoginForm(Stage primaryStage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(10));
        loginLayout.setMaxWidth(300);
        loginLayout.setMaxHeight(400);

        Label errorLabel = new Label(""); // Label pour afficher les messages d'erreur
        errorLabel.setTextFill(Color.RED); // Couleur rouge pour les messages d'erreur

        Label emailLabel = new Label("Email :");
        TextField emailField = new TextField();
        emailField.setPromptText("Entrez votre email");

        Label passwordLabel = new Label("Mot de passe :");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");

        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> {
            AuthenticationResult result = authenticate(emailField.getText(), passwordField.getText());
            if (result.isAuthenticated()) {
                if (result.getStatus() == 0) {
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("Compte bloqué ! Contacter l'admin"); 
                    
                    // Créer le lien "Contacter l'administrateur"
                    Hyperlink contactAdminLink = new Hyperlink("Contacter l'administrateur");
                    contactAdminLink.setOnAction(event -> {
                        // Récupérer la scène actuelle du Stage
                        Scene currentScene = primaryStage.getScene();
                        
                        // Créer l'alerte avec la scène actuelle comme propriétaire
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.initOwner(currentScene.getWindow());
                        alert.setTitle("Contacter l'administrateur");
                        alert.setHeaderText(null);
                        alert.setContentText("Veuillez contacter l'administrateur sur l'adresse mail : admin@admin.com");
                        
                        // Afficher l'alerte
                        alert.showAndWait();
                    });

                    // Ajouter le lien au layout
                    loginLayout.getChildren().add(contactAdminLink);
                } else {
                    MainScreen.userId = result.getUserId();
                    if (result.getRole() == UserRole.ADMIN) {
                        Scene adminPageScene = AdminPage.createAdminPageScene(primaryStage, mainScene);
                        primaryStage.setScene(adminPageScene);
                        primaryStage.setFullScreen(true);
                    } else if (result.getRole() == UserRole.USER) {
                        Scene mainPageScene = MainPage.createMainPageScene(primaryStage, mainScene);
                        primaryStage.setScene(mainPageScene);
                        primaryStage.setFullScreen(true);
                    }
                }
            } else {
                errorLabel.setText(result.getErrorMessage()); 
            }
        });

        Button backButton = new Button("Retour");
        backButton.setOnAction(e -> {
            primaryStage.setScene(mainScene);
            primaryStage.setFullScreen(true);
        });

        // Apply some styling using CSS
        loginLayout.getStyleClass().add("form-container");
        emailLabel.getStyleClass().add("form-label");
        passwordLabel.getStyleClass().add("form-label");
        loginButton.getStyleClass().add("form-button");
        backButton.getStyleClass().add("form-button");

        // Ajouter les éléments au conteneur VBox
        loginLayout.getChildren().addAll(errorLabel, emailLabel, emailField, passwordLabel, passwordField, loginButton, backButton);

        BorderPane outerContainer = new BorderPane();
        outerContainer.setCenter(loginLayout);

        Scene loginScene = new Scene(outerContainer, primaryStage.getWidth(), primaryStage.getHeight());
        loginScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); 
        return loginScene;
    }




    private static class AuthenticationResult {
        private final boolean authenticated;
        private final UserRole role;
        private final int userId;
        private final int statut;
        private final String errorMessage;

        public AuthenticationResult(boolean authenticated, UserRole role, int userId, int statut, String errorMessage) {
            this.authenticated = authenticated;
            this.role = role;
            this.userId = userId;
            this.statut = statut;
            this.errorMessage = errorMessage;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public UserRole getRole() {
            return role;
        }

        public int getUserId() {
            return userId;
        }
        
        public int getStatus() {
            return statut;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }



    private enum UserRole {
        USER,
        ADMIN
    }
}


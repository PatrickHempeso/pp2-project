package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final String DATA_FILE = "C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\data.csv";


    public void init() {
        // Initialization logic if needed
    }

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isValidUser(username, password, DATA_FILE)) {
            if (username.equals("admin")) {
                openManageStockScreen();
            } else {
                openCashierScreen();
            }
        } else {
            showErrorAlert("Invalid username or password.");
        }
    }

    private boolean isValidUser(String username, String password, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String storedUsername = data[1];
                String storedPassword = data[2];
                String storedName = data [3];
                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openManageStockScreen() {
        try {
            // Load the FXML file for the ManageStock screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StockManager.fxml"));
            Parent root = loader.load();

            // Create a new stage for the ManageStock screen
            Stage StockManagerstage = new Stage();
            StockManagerstage.setScene(new Scene(root));
            StockManagerstage.setTitle("Manage Stock");
            StockManagerstage.show();

            // Close the Login.fxml file
            Stage loginStage = (Stage) usernameField.getScene().getWindow(); 
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openCashierScreen() {
    	try {
    	 FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
         Parent root = loader.load();

         // Create a new stage for the ManageStock screen
         // Create a new stage for the ManageStock screen
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         stage.setTitle("Cashier");
         stage.show();

         // Close the Login.fxml file
         Stage loginStage = (Stage) usernameField.getScene().getWindow(); 
         loginStage.close();
     } catch (IOException e) {
         e.printStackTrace();
     }
 }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

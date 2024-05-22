package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class StockManagerController {

    @FXML
    private ListView<String> listView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    @FXML
    private TextArea descriptionField;


    @FXML
    private Button removeButton;

    @FXML
    private Button editButton;

    @FXML
    private Button logoutButton;
    

    private StockManagerBackend backend;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        categoryChoiceBox.getItems().addAll("CPU", "RAM", "GPU", "Motherboard", "PSU", "Storage");
        init();

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateFieldsFromSelection();
            }
        });
    }

    private void init() {
        backend = new StockManagerBackend();
        backend.loadStockItemsFromFile("C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\stocks.csv");
        updateListView();
    }

    @FXML
    private void addStockItem() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String category = categoryChoiceBox.getValue();
        String description = descriptionField.getText().trim(); // Get description text
        
        // Check if any field is empty
        if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || category == null || description.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Please fill all fields and select a category.");
            return;
        }
        
        // Check if the item already exists
        if (backend.itemExists(name)) {
            showAlert(AlertType.ERROR, "Error", "A stock item with the same name already exists.");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);
            backend.addStockItem(name, price, quantity, category, description); // Pass description to the backend
            backend.saveStockItemsToFile("C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\stocks.csv");
            updateListView();
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Invalid Quantity or Price!!");
        }
    }

    @FXML
    private void removeStockItem() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            backend.removeStockItem(selectedIndex);
            backend.saveStockItemsToFile("C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\stocks.csv");
            updateListView();
        } else {
            showAlert(AlertType.WARNING, "Warning", "No item selected.");
        }
    }

    @FXML
    private void editStockItem() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String category = categoryChoiceBox.getValue();
            String description = descriptionField.getText().trim(); // Get description text

            if (name.isEmpty() || priceText.isEmpty() || quantityText.isEmpty() || category == null || description.isEmpty()) {
                showAlert(AlertType.ERROR, "Error", "Please fill all fields and select a category.");
                return;
            }

            if (backend.itemExists(name) && !backend.getStockItems().get(selectedIndex).getName().equalsIgnoreCase(name)) {
                showAlert(AlertType.ERROR, "Error", "A stock item with the same name already exists.");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);
                backend.updateStockItem(selectedIndex, name, price, quantity, category, description);
                backend.saveStockItemsToFile("C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\stocks.csv");
                updateListView();
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Error", "Invalid Quantity or Price!!");
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "No item selected to edit.");
        }
    }

    @FXML
    private void logoutManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addImage() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Name is Empty!");
            return;
        }
        
        // Ensure the imgsample folder exists
        File imgSampleFolder = new File("C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\imgsample");
        if (!imgSampleFolder.exists()) {
            imgSampleFolder.mkdir();
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image File");
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            // Construct the destination file path
            String destFilePath = "C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\imgsample" + File.separator + name + getFileExtension(selectedFile.getName());
            File destFile = new File(destFilePath);
            
            // Copy the selected file to the imgsample folder and rename it
            try {
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                backend.addImage(name, destFile); // Pass the destination file to the backend
            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Error", "Failed to copy image file.");
                e.printStackTrace();
            }
        }
    }

    // Helper method to extract file extension
    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1) {
            return ""; // No extension found
        }
        return filename.substring(lastIndex);
    }


    @FXML
    private void displayCPU() {
        displayItemsByCategory("CPU");
    }

    @FXML
    private void displayRAM() {
        displayItemsByCategory("RAM");
    }

    @FXML
    private void displayGPU() {
        displayItemsByCategory("GPU");
    }

    @FXML
    private void displayStorage() {
        displayItemsByCategory("Storage");
    }

    @FXML
    private void displayPSU() {
        displayItemsByCategory("PSU");
    }

    @FXML
    private void displayMotherboard() {
        displayItemsByCategory("Motherboard");
    }

    @FXML
    private void displayAll() {
        displayAllItems();
    }

    private void displayItemsByCategory(String category) {
        listView.getItems().clear();
        for (StockManagerBackend.StockItem item : backend.getStockItems()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                listView.getItems().add(formatStockItem(item));
            }
        }
    }

    private void displayAllItems() {
        listView.getItems().clear();
        for (StockManagerBackend.StockItem item : backend.getStockItems()) {
            listView.getItems().add(formatStockItem(item));
        }
    }

    private void updateListView() {
        displayAllItems();
    }

    private String formatStockItem(StockManagerBackend.StockItem item) {
        return item.getName() + ", " + item.getPrice() + ", " + item.getQuantity() + ", " + item.getCategory() + ", " + item.getDescription();
    }




    private void updateFieldsFromSelection() {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] parts = selectedItem.split(", ");
            String name = parts[0];
            String price = parts[1];
            String quantity = parts[2];
            String category = parts[3];
            String description = parts[4];

            nameField.setText(name);
            priceField.setText(price);
            quantityField.setText(quantity);
            categoryChoiceBox.setValue(category);
            descriptionField.setText(description); // Set description field
        }
    }


    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button logoutButton;

    @FXML
    private ListView<String> listView;

    @FXML
    private Label nameLabel;
    @FXML
    private Label quantityLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView itemImageView;

    @FXML
    private TableView<CartItem> cartTableView;

    @FXML
    private TableColumn<CartItem, String> itemNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> itemQuantityColumn;

    @FXML
    private TableColumn<CartItem, Double> itemPriceColumn;

    private List<StockItem> stockItems;
    
    private ObservableList<CartItem> cartItems;

    @FXML
    private void logoutCashier() {
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
    private void displayCPU(ActionEvent event) {
        displayItemsByCategory("CPU");
    }

    @FXML
    private void displayRAM(ActionEvent event) {
        displayItemsByCategory("RAM");
    }

    @FXML
    private void displayGPU(ActionEvent event) {
        displayItemsByCategory("GPU");
    }

    @FXML
    private void displayPSU(ActionEvent event) {
        displayItemsByCategory("PSU");
    }

    @FXML
    private void displayMotherboard(ActionEvent event) {
        displayItemsByCategory("Motherboard");
    }

    @FXML
    private void displayStorage(ActionEvent event) {
        displayItemsByCategory("Storage");
    }

    private void displayItemsByCategory(String category) {
        List<String> filteredItems = stockItems.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .map(StockItem::getName)
                .collect(Collectors.toList());
        listView.getItems().setAll(filteredItems);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stockItems = loadStockItemsFromFile("C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\stocks.csv");
        cartItems = FXCollections.observableArrayList();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                StockItem selectedItem = stockItems.stream()
                        .filter(item -> item.getName().equals(newValue))
                        .findFirst()
                        .orElse(null);
                if (selectedItem != null) {
                    nameLabel.setText("Name: " + selectedItem.getName());
                    quantityLabel.setText("Quantity: " + selectedItem.getQuantity());
                    priceLabel.setText("Price: ₱" + selectedItem.getPrice());
                    descriptionLabel.setText("Description: " + selectedItem.getDescription());

                    // Load and display the image
                    String folderPath = "C:\\Users\\hempe\\OneDrive\\Desktop\\pp2\\data\\imgsample\\";
                    String imageName = selectedItem.getName() + ".png";
                    String imagePath = folderPath + imageName;
                    Image image = new Image(new File(imagePath).toURI().toString());
                    itemImageView.setImage(image);

                }
            }
        });

        // Initialize cart table columns
        itemNameColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        itemQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        itemPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        cartTableView.setItems(cartItems);
    }

    private List<StockItem> loadStockItemsFromFile(String filePath) {
        List<StockItem> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header line
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    String category = parts[3];
                    String description = parts[4];
                    items.add(new StockItem(name, price, quantity, category, description));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    @FXML
    private void addItemToCart() {
        String selectedItemName = listView.getSelectionModel().getSelectedItem();
        if (selectedItemName != null) {
            StockItem selectedStockItem = stockItems.stream()
                    .filter(item -> item.getName().equals(selectedItemName))
                    .findFirst()
                    .orElse(null);

            if (selectedStockItem != null) {
                boolean itemExists = false;

                // Check if the item already exists in the cart
                for (CartItem cartItem : cartItems) {
                    if (cartItem.getDescription().equals(selectedItemName)) {
                        // Update quantity and total price
                        cartItem.setQuantity(cartItem.getQuantity() + 1);
                        cartItem.setPrice(cartItem.getPrice() + selectedStockItem.getPrice());
                        itemExists = true;
                        break;
                    }
                }

                // If the item doesn't exist, add it to the cart
                if (!itemExists) {
                    CartItem cartItem = new CartItem(selectedStockItem.getName(), selectedStockItem.getPrice(), 1);
                    cartItems.add(cartItem);
                }
            }
        }
    }

    @FXML
    private void removeItemFromCart() {
        CartItem selectedCartItem = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedCartItem != null) {
            cartItems.remove(selectedCartItem);
        }
    }
    
    @FXML
    private void cancelOrder(ActionEvent event) {
        // Clear the cart items
        cartItems.clear();
        cartTableView.refresh();
    }

    @FXML
    private void checkOutCart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payment.fxml"));
            Parent root = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setCartItems(cartItems);  // Pass cartItems to PaymentController

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Payment");
            stage.show();

            // Close the current window (optional)
            Stage currentStage = (Stage) listView.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class StockItem {
        private final String name;
        private final double price;
        private final int quantity;
        private final String category;
        private final String description;

        public StockItem(String name, double price, int quantity, String category, String description) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }
    }
}

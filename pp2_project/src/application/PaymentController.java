package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private ListView<String> listView;

    @FXML
    private TableView<CartItem> cartView;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> priceColumn;

    @FXML
    private TableColumn<CartItem, String> descriptionColumn;

    @FXML
    private TableColumn<CartItem, Double> totalColumn;

    @FXML
    private Label paymentLabel;

    @FXML
    private Label amountPaidLabel;

    @FXML
    private Label changeLabel;

    @FXML
    private Label totalAmountLabel;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        cartView.setItems(cartItems);
        updateTotalAmountLabel();
    }
    
    public void setCartItems(ObservableList<CartItem> cartItems2) {
        cartItems.setAll(cartItems2); // Use the provided cart items
        updateTotalAmountLabel();
    }

    private void updateTotalAmountLabel() {
        double totalAmount = cartItems.stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum();
        totalAmountLabel.setText("Total amount: ₱" + String.format("%.2f", totalAmount));
    }

    @FXML
    private void calculatePayment(ActionEvent event) {
        try {
            String paymentText = paymentLabel.getText().replaceAll("\\₱", "").replaceAll(",", "");
            double paymentAmount = Double.parseDouble(paymentText);
            double totalAmount = cartItems.stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum();

            if (paymentAmount >= totalAmount) {
                double change = paymentAmount - totalAmount;
                changeLabel.setText("Change: ₱" + String.format("%.2f", change));
            } else {
                changeLabel.setText("Insufficient payment");
            }
        } catch (NumberFormatException e) {
            amountPaidLabel.setText("Invalid input");
            changeLabel.setText("");
        }
    }

    @FXML
    private void newOrder(ActionEvent event) {
        // Close the current payment window
        Stage stage = (Stage) amountPaidLabel.getScene().getWindow();
        stage.close();

        // Open the dashboard window to create a new order
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(scene);
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void enter0(ActionEvent event) {
        appendToPaymentLabel("0");
    }

    @FXML
    private void enter1(ActionEvent event) {
        appendToPaymentLabel("1");
    }

    @FXML
    private void enter2(ActionEvent event) {
        appendToPaymentLabel("2");
    }

    @FXML
    private void enter3(ActionEvent event) {
        appendToPaymentLabel("3");
    }

    @FXML
    private void enter4(ActionEvent event) {
        appendToPaymentLabel("4");
    }

    @FXML
    private void enter5(ActionEvent event) {
        appendToPaymentLabel("5");
    }

    @FXML
    private void enter6(ActionEvent event) {
        appendToPaymentLabel("6");
    }

    @FXML
    private void enter7(ActionEvent event) {
        appendToPaymentLabel("7");
    }

    @FXML
    private void enter8(ActionEvent event) {
        appendToPaymentLabel("8");
    }

    @FXML
    private void enter9(ActionEvent event) {
        appendToPaymentLabel("9");
    }
    
    @FXML
    private void enterButton(ActionEvent event) {
        calculatePayment(event);
        amountPaidLabel(); // Call amountPaidLabel() after payment calculation
    }

    @FXML
    private void enterClear(ActionEvent event) {
        paymentLabel.setText("");
    }

    @FXML
    private void enterBackspace(ActionEvent event) {
        String currentText = paymentLabel.getText();
        if (!currentText.isEmpty()) {
            paymentLabel.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void appendToPaymentLabel(String text) {
        String currentText = paymentLabel.getText();
        paymentLabel.setText(currentText + text);
    }

    private void amountPaidLabel() {
        String paymentText = paymentLabel.getText().replaceAll("\\₱", "").replaceAll(",", "");
        amountPaidLabel.setText("Amount Paid: ₱" + paymentText);
    }

}

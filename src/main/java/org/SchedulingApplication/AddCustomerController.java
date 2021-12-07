package org.SchedulingApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.SchedulingApplication.Model.ComboBoxItem;
import org.SchedulingApplication.Utilities.ComboBoxFiller;
import org.SchedulingApplication.Utilities.DBConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {

    Connection connection = DBConnector.getConnection();

    @FXML
    private TextField customerIDTextField;

    @FXML
    private TextField customerNameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField postalTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private ComboBox<ComboBoxItem> countryComboBox;

    @FXML
    private ComboBox<ComboBoxItem> divisionComboBox;

    @FXML
    public void onActionCountryComboBox() {

        divisionComboBox.setDisable(false);
        String countryID = String.valueOf(countryComboBox.getValue().getId());
        divisionComboBox.setItems(ComboBoxFiller.requestGenericList("divisions", "division_name",
                "division_id", countryID, false, false));
   }

    @FXML
    public void onActionCancel(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Main Screen");
    }

    @FXML
    public void onActionSave(ActionEvent event) throws IOException {

        String user = LoginController.getCurrentUserName();

        String customerName = customerNameTextField.getText();
        String address = addressTextField.getText();
        String postalCode = postalTextField.getText();
        String phone = phoneTextField.getText();
        String email = emailTextField.getText();
        String divisionID;

        try {
            if (customerName.isEmpty()) {
                throw new Exception("Please enter value for Customer Name.");
            }
            if (address.isEmpty()) {
                throw new Exception("Please enter value for Address.");
            }
            if (postalCode.isEmpty()) {
                throw new Exception("Please enter value for Postal Code.");
            }
            if (phone.isEmpty()) {
                throw new Exception("Please enter value for Phone Number.");
            }
            if (email.isEmpty()) {
                throw new Exception("Please enter value for E-mail.");
            }
            if (countryComboBox.getValue() == null) {
                throw new Exception("Please select a Country.");
            }
            if (divisionComboBox.getValue() == null) {
                throw new Exception("Please select a State/Province.");
            }
        }
        catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        divisionID = String.valueOf(divisionComboBox.getValue().getId());

        // gets the current highest customer_id value from database
        String maxSQL = "SELECT MAX(customer_id) AS max " +
                        "FROM customers;";

        // insert new customer entity into the database
        String insertSQL = "INSERT INTO customers VALUES(?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?);";

        // lock required because customer_id in database is INT not SERIAL
        try (PreparedStatement lockStatement = connection.prepareStatement("LOCK TABLES customers WRITE;");
             PreparedStatement maxStatement = connection.prepareStatement(maxSQL);
             PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {

            lockStatement.execute();

            int currentCustomerID = -1;

            try (ResultSet maxResults = maxStatement.executeQuery()) {
                while (maxResults.next()) {
                    currentCustomerID = maxResults.getInt("max");
                }
                currentCustomerID++;
            }
            catch (SQLException se2) {
                se2.printStackTrace();
                return;
            }

            insertStatement.setInt(1, currentCustomerID);
            insertStatement.setString(2, customerName);
            insertStatement.setString(3, address);
            insertStatement.setString(4, postalCode);
            insertStatement.setString(5, phone);
            insertStatement.setString(6, email);
            insertStatement.setString(7, user);
            insertStatement.setString(8, user);
            insertStatement.setString(9, divisionID);

            insertStatement.execute();
            System.out.println(insertStatement.getUpdateCount() + " row(s) affected in database.");
        }
        catch (SQLException se1) {
            se1.printStackTrace();
            return;
        }
        finally {
            try (PreparedStatement unlockStatement = connection.prepareStatement("UNLOCK TABLES;")) {
                unlockStatement.execute();
            }
            catch (SQLException se3) {
                se3.printStackTrace();
            }
        }

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Main Screen");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFormatter<String> nameFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> addressFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> postalCodeFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> phoneFormatter = new TextFormatter<>(change -> {
            if ((!change.getText().matches("[0-9-]+")) || (change.getControlNewText().length() > 50)) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> emailFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        customerIDTextField.setDisable(true);
        customerIDTextField.setText("auto assigned");

        customerNameTextField.setTextFormatter(nameFormatter);
        addressTextField.setTextFormatter(addressFormatter);
        postalTextField.setTextFormatter(postalCodeFormatter);
        phoneTextField.setTextFormatter(phoneFormatter);
        emailTextField.setTextFormatter(emailFormatter);

        countryComboBox.setItems(ComboBoxFiller.requestGenericList("countries", "country_name",
                "country_id", null, false, false));
        divisionComboBox.setDisable(true);
    }
}

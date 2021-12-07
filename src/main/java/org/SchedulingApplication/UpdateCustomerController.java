package org.SchedulingApplication;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.SchedulingApplication.Model.ComboBoxItem;
import org.SchedulingApplication.Model.Customer;
import org.SchedulingApplication.Utilities.ComboBoxFiller;
import org.SchedulingApplication.Utilities.DBConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable {

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

    public void receiveCustomer(Customer customer) {

        customerIDTextField.setText(String.valueOf(customer.getCustomerID()));
        customerNameTextField.setText(customer.getName());
        addressTextField.setText(customer.getAddress());
        postalTextField.setText(customer.getPostalCode());
        phoneTextField.setText(customer.getPhone());
        emailTextField.setText(customer.getEmail());

        ObservableList<ComboBoxItem> countryList = ComboBoxFiller.requestGenericList("countries",
                "country_name", "country_id", null, false, false);
        ComboBoxItem countryComboItem = null;

        for(ComboBoxItem country : countryList) {
            if(country.getName().equals(customer.getCountryName())) {
                countryComboItem = country;
                break;
            }
        }
        countryComboBox.setItems(countryList);
        countryComboBox.getSelectionModel().select(countryComboItem);

        assert countryComboItem != null;
        String countryID = String.valueOf(countryComboItem.getId());

        ObservableList<ComboBoxItem> divisionList = ComboBoxFiller.requestGenericList("divisions",
                "division_name", "division_id", countryID, false, false);
        ComboBoxItem divisionComboItem = null;

        for(ComboBoxItem division : divisionList) {
            if(division.getName().equals(customer.getDivisionName())) {
                divisionComboItem = division;
                break;
            }
        }
        divisionComboBox.setItems(divisionList);
        divisionComboBox.getSelectionModel().select(divisionComboItem);
    }

    @FXML
    public void onActionCountryComboBox() {

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

        String customerID = customerIDTextField.getText();
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

        String sql = "UPDATE customers SET customer_name = ?, address = ?, postal_code = ?, phone = ?, " +
                     "email = ?, last_update = NOW(), last_updated_by = ?, division_id = ? WHERE customer_id = ?;";

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql)) {

            statement.setString(1, customerName);
            statement.setString(2, address);
            statement.setString(3, postalCode);
            statement.setString(4, phone);
            statement.setString(5, email);
            statement.setString(6, user);
            statement.setString(7, divisionID);
            statement.setString(8, customerID);

            statement.execute();
            System.out.println(statement.getUpdateCount() + " row(s) affected in database.");
        }
        catch (SQLException se) {
            se.printStackTrace();
            return;
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

        customerNameTextField.setTextFormatter(nameFormatter);
        addressTextField.setTextFormatter(addressFormatter);
        postalTextField.setTextFormatter(postalCodeFormatter);
        phoneTextField.setTextFormatter(phoneFormatter);
        emailTextField.setTextFormatter(emailFormatter);
    }
}

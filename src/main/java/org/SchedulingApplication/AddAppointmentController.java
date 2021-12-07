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
import org.SchedulingApplication.Utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {

    @FXML
    private TextField appointmentIDTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private ComboBox<ComboBoxItem> officeComboBox;

    @FXML
    private ComboBox<ComboBoxItem> contactComboBox;

    @FXML
    private ComboBox<ComboBoxItem> typeComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField startTimeTextField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField endTimeTextField;

    @FXML
    private ComboBox<ComboBoxItem> customerIDComboBox;

    @FXML
    private ComboBox<ComboBoxItem> userIDComboBox;

    @FXML
    void onActionStartDatePicker() {

        endDatePicker.setValue(startDatePicker.getValue());
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

        String title = titleTextField.getText();
        String description = descriptionTextArea.getText();
        String office;
        int contactID;
        String type;
        String startTimeString = startTimeTextField.getText();
        String endTimeString = endTimeTextField.getText();
        int customerID;
        int userID;

        try {
            if (title.isEmpty()) {
                throw new Exception("Please enter value for Title.");
            }
            if (description.isEmpty()) {
                throw new Exception("Please enter value for Description.");
            }
            if (officeComboBox.getValue() == null) {
                throw new Exception("Please select a Location.");
            }
            if (contactComboBox.getValue() == null) {
                throw new Exception("Please select a Contact.");
            }
            if (typeComboBox.getValue() == null) {
                throw new Exception("Please select a Meeting Type.");
            }
            if (startDatePicker.getValue() == null) {
                throw new Exception("Please select a Start Date.");
            }
            if (startTimeString.isEmpty()) {
                throw new Exception("Please enter a value for Start Time.");
            }
            if (startTimeString.length() < 4) {  // ensures below .charAt(1) will not throw exception
                throw new Exception("Please enter Start Time in\nvalid HH:mm 24-hour format.");
            }
            if (endDatePicker.getValue() == null) {
                throw new Exception("Please select an End Date.");
            }
            if (endTimeString.isEmpty()) {
                throw new Exception("Please enter a value for End Time.");
            }
            if (endTimeString.length() < 4) {  // ensures below .charAt(1) will not throw exception
                throw new Exception("Please enter End Time in\nvalid HH:mm 24-hour format.");
            }
            if (customerIDComboBox.getValue() == null) {
                throw new Exception("Please select a Customer ID.");
            }
            if (userIDComboBox.getValue() == null) {
                throw new Exception("Please select a User ID.");
            }
        }
        catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        office = officeComboBox.getValue().getName();
        contactID = contactComboBox.getValue().getId();
        type = typeComboBox.getValue().getName();
        customerID = customerIDComboBox.getValue().getId();
        userID = userIDComboBox.getValue().getId();

        if(startTimeString.charAt(1) == ':') {       // allows entry in H:mm format
            startTimeString = '0' + startTimeString;
        }
        if(endTimeString.charAt(1) == ':') {
            endTimeString = '0' + endTimeString;
        }

        LocalTime startTime;
        LocalTime endTime;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

        try {
            startTime = LocalTime.parse(startTimeString, timeFormatter);
            endTime = LocalTime.parse(endTimeString, timeFormatter);
        }
        catch(DateTimeParseException parseException) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please enter Start & End Time\nin valid HH:mm 24-hour format.");
            alert.showAndWait();
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);

        LocalDate endDate = endDatePicker.getValue();
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

        try {
            if (startDateTime.isBefore(LocalDateTime.now())) {
                throw new Exception("Start Date/Time must be set in the future.");
            }
            if (endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime)) {
                throw new Exception("Start Date/Time must be before End Date/Time.");
            }
            if (AppointmentTimeValidator.checkIfNotWithinBusinessHours(startDateTime, endDateTime, office)) {
                throw new Exception("This appointment is not within " + office + "'s\nregular business hours of 08:00 - 22:00.");
            }
            if (AppointmentTimeValidator.checkIfOverlappingAppointment(startDateTime, endDateTime,
                    String.valueOf(customerID), String.valueOf(contactID), null)) {
                throw new Exception("This appointment would overlap\nwith an existing appointment.");
            }
        }
        catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        String sql = "INSERT INTO appointments VALUES(default, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?);";

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setString(2, description);
            statement.setString(3, office);
            statement.setString(4, type);
            statement.setTimestamp(5, startTimestamp);
            statement.setTimestamp(6, endTimestamp);
            statement.setString(7, user);
            statement.setString(8, user);
            statement.setInt(9, customerID);
            statement.setInt(10, userID);
            statement.setInt(11, contactID);

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

        TextFormatter<String> titleFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> descriptionFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> startTimeFormatter = new TextFormatter<>(change -> {
            if ((!change.getText().matches("[0-9:]+"))  || (change.getControlNewText().length() > 5)) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> endTimeFormatter = new TextFormatter<>(change -> {
            if ((!change.getText().matches("[0-9:]+"))  || (change.getControlNewText().length() > 5)) {
                change.setText("");
            }
            return change;
        });

        appointmentIDTextField.setDisable(true);
        appointmentIDTextField.setText("auto assigned");

        titleTextField.setTextFormatter(titleFormatter);
        descriptionTextArea.setTextFormatter(descriptionFormatter);
        startTimeTextField.setTextFormatter(startTimeFormatter);
        endTimeTextField.setTextFormatter(endTimeFormatter);

        officeComboBox.setItems(ComboBoxFiller.requestOfficeList(false));
        contactComboBox.setItems(ComboBoxFiller.requestGenericList("contacts", "contact_name",
                "contact_id", null, false, false));
        typeComboBox.setItems(ComboBoxFiller.requestTypeList());
        customerIDComboBox.setItems(ComboBoxFiller.requestGenericList("customers", "customer_name",
                "customer_id", null, false, true));
        userIDComboBox.setItems(ComboBoxFiller.requestGenericList("users", "user_name",
                "user_id", null, false, true));
    }
}

package org.SchedulingApplication;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.SchedulingApplication.Model.Appointment;
import org.SchedulingApplication.Model.ComboBoxItem;
import org.SchedulingApplication.Model.Customer;
import org.SchedulingApplication.Utilities.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainScreenController implements Initializable {

    Connection connection = DBConnector.getConnection();

    ObservableList<Customer> allCustomers = DBCustomerGetter.getAllCustomers();

    ObservableList<Customer> filteredCustomers = FXCollections.observableArrayList();
    boolean customerFilterPresent = false;

    ObservableList<Appointment> allAppointments = DBAppointmentGetter.getAllAppointments();

    ObservableList<Appointment> searchFilteredAppointments = FXCollections.observableArrayList();
    boolean searchAppointmentFilterPresent = false;

    ObservableList<Appointment> timeFilteredAppointments = FXCollections.observableArrayList();
    boolean timeAppointmentFilterPresent = false;

    ObservableList<Appointment> doubleFilteredAppointments = FXCollections.observableArrayList();

    @FXML
    private TableView<Customer> customerTableView;

    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    @FXML
    private TableColumn<Customer, String> customerAddressColumn;

    @FXML
    private TableColumn<Customer, String> customerPostalCodeColumn;

    @FXML
    private TableColumn<Customer, String> customerPhoneColumn;

    @FXML
    private TableColumn<Customer, String> customerEmailColumn;

    @FXML
    private TableColumn<Customer, String> customerCountryColumn;

    @FXML
    private TableColumn<Customer, Integer> customerDivisionColumn;

    @FXML
    private TextField customerSearchTextField;

    @FXML
    private TableView<Appointment> appointmentTableView;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIDColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentOfficeColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentContactNameColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentStartDateColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentStartTimeColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentEndDateColumn;

    @FXML
    private TableColumn<Appointment, String> appointmentEndTimeColumn;

    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerIDColumn;

    @FXML
    private TextField appointmentSearchTextField;

    @FXML
    private ComboBox<ComboBoxItem> monthComboBox;

    @FXML
    private ComboBox<ComboBoxItem> weekComboBox;

    @FXML
    public void onActionAddCustomer(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Add Customer");
    }

    @FXML
    public void onActionAddAppointment(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Add Appointment");
    }

    @FXML
    public void onActionUpdateCustomer(ActionEvent event) throws IOException {

        if(customerTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a customer to update.");
            alert.showAndWait();
            return;
        }

        // loads UpdateCustomer fxml file in "loader"
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UpdateCustomer.fxml"));
        loader.load();

        // allows use of UpdateCustomer method "receiveCustomer" within loader
        UpdateCustomerController controller = loader.getController();
        controller.receiveCustomer(customerTableView.getSelectionModel().getSelectedItem());

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.setTitle("Update Customer");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void onActionUpdateAppointment(ActionEvent event) throws IOException {

        if(appointmentTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select an appointment to update.");
            alert.showAndWait();
            return;
        }

        // loads UpdateAppointment fxml file in "loader"
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UpdateAppointment.fxml"));
        loader.load();

        // allows use of UpdateAppointment method "receiveAppointment" within loader
        UpdateAppointmentController controller = loader.getController();
        controller.receiveAppointment(appointmentTableView.getSelectionModel().getSelectedItem());

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.setTitle("Update Appointment");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public void onActionDeleteCustomer() {

        Customer customer = customerTableView.getSelectionModel().getSelectedItem();

        if (customer == null) {
            Alert nullAlert = new Alert(Alert.AlertType.WARNING);
            nullAlert.setContentText("Please select a customer to delete.");
            nullAlert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this customer?");
        Optional<ButtonType> confirmationResult = confirmation.showAndWait();

        if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {

            String customerID = String.valueOf(customer.getCustomerID());
            String customerName = customer.getName();

            // delete all appointments in database associated with the selected customer
            String appointmentSQL = "DELETE FROM appointments " +
                                    "WHERE customer_id = ?;";

            // delete the selected customer from the database
            String customerSQL = "DELETE FROM customers " +
                                 "WHERE customer_id = ?;";

            // log the customer deletion activity in the database
            String deletionActivitySQL = "INSERT INTO database_deletion_activity " +
                                         "VALUES(default, ?, ?, NOW());";

            try (PreparedStatement appointmentStatement = connection.prepareStatement(appointmentSQL);
                 PreparedStatement customerStatement = connection.prepareStatement(customerSQL);
                 PreparedStatement deletionActivityStatement = connection.prepareStatement(deletionActivitySQL)) {

                appointmentStatement.setString(1, customerID);
                appointmentStatement.execute();

                customerStatement.setString(1, customerID);
                customerStatement.execute();

                String updateTotal = String.valueOf(appointmentStatement.getUpdateCount() + customerStatement.getUpdateCount());
                System.out.println(updateTotal + " row(s) affected in database.");

                deletionActivityStatement.setString(1, LoginController.getCurrentUserID());
                deletionActivityStatement.setString(2, "CUSTOMER " + customerID);
                deletionActivityStatement.execute();

                // remove all appointments from the the active ObservableLists
                allAppointments.removeIf(appointment -> appointment.getCustomerID() == customer.getCustomerID());

                if (searchAppointmentFilterPresent) {
                    searchFilteredAppointments.removeIf(appointment -> appointment.getCustomerID() == customer.getCustomerID());
                }

                // remove the customer from the active ObservableLists
                allCustomers.remove(customer);

                if (customerFilterPresent) {
                    filteredCustomers.remove(customer);
                }

                Alert notifyAlert = new Alert(Alert.AlertType.WARNING);
                notifyAlert.setTitle("CUSTOMER DELETED");
                notifyAlert.setContentText("Customer " + customerID + ": " + customerName + " has been deleted.");
                notifyAlert.showAndWait();
            }
            catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    @FXML
    public void onActionDeleteAppointment() {

        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if(appointment == null) {
            Alert nullAlert = new Alert(Alert.AlertType.WARNING);
            nullAlert.setContentText("Please select an appointment to delete.");
            nullAlert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this appointment?");
        Optional<ButtonType> confirmationResult = confirmation.showAndWait();

        if(confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {

            String appointmentID = String.valueOf(appointment.getAppointmentID());
            String location = appointment.getOffice();

            // delete the selected appointment from the database
            String deletionSQL = "DELETE FROM appointments " +
                                 "WHERE appointment_id = ?;";

            // log the appointment deletion activity in the database
            String deletionActivitySQL = "INSERT INTO database_deletion_activity " +
                                         "VALUES(default, ?, ?, NOW());";

            try (PreparedStatement deletionStatement = connection.prepareStatement(deletionSQL);
                 PreparedStatement deletionActivityStatement = connection.prepareStatement(deletionActivitySQL)){

                deletionStatement.setString(1, appointmentID);
                deletionStatement.execute();

                System.out.println(deletionStatement.getUpdateCount() + " row(s) affected in database.");

                deletionActivityStatement.setString(1, LoginController.getCurrentUserID());
                deletionActivityStatement.setString(2, "APPOINTMENT " + appointmentID);
                deletionActivityStatement.execute();

                // remove the appointment from the active ObservableLists
                allAppointments.remove(appointment);

                if(searchAppointmentFilterPresent) {
                    searchFilteredAppointments.remove(appointment);
                }

                Alert notifyAlert = new Alert(Alert.AlertType.WARNING);
                notifyAlert.setTitle("APPOINTMENT CANCELLED");
                notifyAlert.setContentText("Appointment " + appointmentID + " in " + location + " has been cancelled.");
                notifyAlert.showAndWait();
            }
            catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    @FXML
    public void customerSearchTextChanged() {

        filteredCustomers.clear();  // clears any previous search filter

        String searchText = customerSearchTextField.getText().toUpperCase();  // allows search to ignore case

        if(searchText.isEmpty()) {
            customerFilterPresent = false;
            customerTableView.setItems(allCustomers);
        }
        else {
            customerFilterPresent = true;   // allows onActionDeleteCustomer() to know a search filter is present

            // if any customer attribute contains the searchText, add it to filteredCustomers
            for(Customer customer : allCustomers) {
                if (String.valueOf(customer.getCustomerID()).contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getName().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getAddress().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getPhone().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getPostalCode().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getEmail().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getCountryName().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
                else if (customer.getDivisionName().toUpperCase().contains(searchText)) {
                    filteredCustomers.add(customer);
                }
            }
//            filteredCustomers = allCustomers.stream().filter(customer ->
//                    String.valueOf(customer.getCustomerID()).contains(searchText) ||
//                    customer.getName().toUpperCase().contains(searchText) ||
//                    customer.getAddress().toUpperCase().contains(searchText) ||
//                    customer.getPhone().toUpperCase().contains(searchText) ||
//                    customer.getPostalCode().toUpperCase().contains(searchText) ||
//                    customer.getEmail().toUpperCase().contains(searchText) ||
//                    customer.getCountryName().toUpperCase().contains(searchText) ||
//                    customer.getDivisionName().toUpperCase().contains(searchText)).collect(Collectors.toCollection(FXCollections::observableList));

            customerTableView.setItems(filteredCustomers);
        }
    }

    @FXML
    public void appointmentSearchTextChanged() {

        String searchText = appointmentSearchTextField.getText().toUpperCase();  // allows search to ignore case

        if (searchText.isEmpty() && !timeAppointmentFilterPresent) {
            searchAppointmentFilterPresent = false;
            appointmentTableView.setItems(allAppointments);
        }
        else if (searchText.isEmpty()) {
            searchAppointmentFilterPresent = false;
            appointmentTableView.setItems(timeFilteredAppointments);
        }
        else if (!timeAppointmentFilterPresent){

            searchFilteredAppointments.clear();        // clears any previous search filter
            searchAppointmentFilterPresent = true;     // allows other methods to know a search filter is present

            // if any appointment attribute contains the searchText, add it to filteredAppointments
            for (Appointment appointment : allAppointments) {
                if (String.valueOf(appointment.getAppointmentID()).contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getTitle().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getDescription().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getOffice().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getContactName().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getType().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getStartDateString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getStartTimeString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getEndDateString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (appointment.getEndTimeString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
                else if (String.valueOf(appointment.getCustomerID()).contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                }
            }
            appointmentTableView.setItems(searchFilteredAppointments);
        }
        else {
            searchFilteredAppointments.clear();     // clears any previous filters
            doubleFilteredAppointments.clear();
            searchAppointmentFilterPresent = true;  // allows other methods to know a search filter is present

            // appointments are added to doubleFilteredAppointments if searchText is found in appointment's attributes
            // and that appointment is also found in timeFilteredAppointments, meaning appointment fits both filters

            // appointments are also added to searchFilteredAppointments in case the time filter is ever removed
            for (Appointment appointment : allAppointments) {
                if (String.valueOf(appointment.getAppointmentID()).contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getTitle().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getDescription().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getOffice().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getContactName().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getType().toUpperCase().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getStartDateString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getStartTimeString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getEndDateString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (appointment.getEndTimeString().contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
                else if (String.valueOf(appointment.getCustomerID()).contains(searchText)) {
                    searchFilteredAppointments.add(appointment);
                    if(timeFilteredAppointments.contains(appointment)) {
                        doubleFilteredAppointments.add(appointment);
                    }
                }
            }
            appointmentTableView.setItems(doubleFilteredAppointments);
        }
    }

    @FXML
    public void onActionMonthComboBox() {

        if(monthComboBox.getValue().getName().equals("ALL")) {
            weekComboBox.setItems(null);
            weekComboBox.setDisable(true);

            timeAppointmentFilterPresent = false;

            if(searchAppointmentFilterPresent) {
                appointmentTableView.setItems(searchFilteredAppointments);
            }
            else {
                appointmentTableView.setItems(allAppointments);
            }
        }
        else {
            String month = monthComboBox.getValue().getName();

            weekComboBox.setDisable(false);
            weekComboBox.setItems(ComboBoxFiller.requestWeekList(month));
            weekComboBox.getSelectionModel().selectFirst();  // selects "ALL", and allows all time filtering
        }                                                    // to be handled within onActionWeekComboBox()
    }

    @FXML
    public void onActionWeekComboBox() {

        if(weekComboBox.getValue() == null) {  // this stops weekComboBox.setItems() from triggering this method
            return;
        }
        timeFilteredAppointments.clear();      // clears any previous time filter
        timeAppointmentFilterPresent = true;   // allows other methods to know a time filter is present

        if(weekComboBox.getValue().getName().equals("ALL")) {

            if(!searchAppointmentFilterPresent) {

                String month = String.valueOf(monthComboBox.getValue().getId());
                if(month.length() == 1) {
                    month = '0' + month;
                }

                // adds appointment to timeFilteredAppointments if it's start month is same as selected month
                for(Appointment appointment : allAppointments) {
                    if(appointment.getStartDateString().substring(0, 2).equals(month)) {
                        timeFilteredAppointments.add(appointment);
                    }
                }
                appointmentTableView.setItems(timeFilteredAppointments);
            }
            else {
                doubleFilteredAppointments.clear();  // clears any previous double filter

                String month = String.valueOf(monthComboBox.getValue().getId());
                if(month.length() == 1) {
                    month = "0" + month;
                }

                // adds appointment to doubleFilteredAppointments if it's start month is same as selected month, and
                // that appointment is also present is searchFilteredAppointments, meaning it fits both filters

                // appointments are also added to timeFilteredAppointments in case search filter is ever removed
                for(Appointment appointment : allAppointments) {
                    if(appointment.getStartDateString().substring(0, 2).equals(month)) {
                        timeFilteredAppointments.add(appointment);
                        if(searchFilteredAppointments.contains(appointment)) {
                            doubleFilteredAppointments.add(appointment);
                        }
                    }
                }
                appointmentTableView.setItems(doubleFilteredAppointments);
            }
        }
        else {
            ObservableList<Appointment> monthFilteredAppointments = FXCollections.observableArrayList();

            if(!searchAppointmentFilterPresent) {

                String month = String.valueOf(monthComboBox.getSelectionModel().getSelectedItem().getId());
                if(month.length() == 1) {
                    month = "0" + month;
                }

                // adds appointment to monthFilteredAppointments if it's start month is same as selected month
                for(Appointment appointment : allAppointments) {
                    if(appointment.getStartDateString().substring(0, 2).equals(month)) {
                        monthFilteredAppointments.add(appointment);
                    }
                }

                int endDayOfWeek = weekComboBox.getValue().getId();  // ID values hold multiples of 7 per every week
                int startDayOfWeek = endDayOfWeek - 6;

                String stringStartDay;

                // monthFilteredAppointments now filtered by week
                for(Appointment appointment : monthFilteredAppointments) {

                    stringStartDay = appointment.getStartDateString().substring(3, 5);
                    if(stringStartDay.charAt(0) == '0') {
                        stringStartDay = stringStartDay.substring(1);
                    }

                    int appointmentStartDay = Integer.parseInt(stringStartDay);

                    // appointments added if their start day falls within the selected week
                    if((startDayOfWeek <= appointmentStartDay) && (appointmentStartDay <= endDayOfWeek)) {
                        timeFilteredAppointments.add(appointment);
                    }
                }
                appointmentTableView.setItems(timeFilteredAppointments);
            }
            else {
                doubleFilteredAppointments.clear();  // clears any previous double filter

                String month = String.valueOf(monthComboBox.getSelectionModel().getSelectedItem().getId());
                if(month.length() == 1) {
                    month = "0" + month;
                }

                // adds appointment to monthFilteredAppointments if it's start month is same as selected month
                for(Appointment appointment : allAppointments) {
                    if(appointment.getStartDateString().substring(0, 2).equals(month)) {
                        monthFilteredAppointments.add(appointment);
                    }
                }

                int endDayOfWeek = weekComboBox.getValue().getId();  // ID values hold multiples of 7 per every week
                int startDayOfWeek = endDayOfWeek - 6;

                String stringStartDay;

                // monthFilteredAppointments now filtered by week
                for(Appointment appointment : monthFilteredAppointments) {

                    stringStartDay = appointment.getStartDateString().substring(3, 5);
                    if(stringStartDay.charAt(0) == '0') {
                        stringStartDay = stringStartDay.substring(1);
                    }

                    int appointmentStartDay = Integer.parseInt(stringStartDay);

                    // appointments added to doubleFilteredAppointments if  their start day falls within the selected
                    // week, and appointment is also present in searchFilteredAppointments, meaning it fits both filters

                    // appointments are also added to timeFilteredAppointments in case search filter is ever removed
                    if((startDayOfWeek <= appointmentStartDay) && (appointmentStartDay <= endDayOfWeek)) {
                        timeFilteredAppointments.add(appointment);
                        if(searchFilteredAppointments.contains(appointment)) {
                            doubleFilteredAppointments.add(appointment);
                        }
                    }
                }
                appointmentTableView.setItems(doubleFilteredAppointments);
            }
        }
    }

    @FXML
    public void onActionReports(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("Reports.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Reports");
    }

    @FXML
    public void onActionLogOut(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Scheduling Login");
    }

    @FXML
    public void onActionExit() {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        monthComboBox.setItems(ComboBoxFiller.requestMonthList());
        monthComboBox.getSelectionModel().selectFirst();  // monthComboBox will start on "ALL"

        weekComboBox.setDisable(true);

        customerTableView.setItems(allCustomers);
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerCountryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

        appointmentTableView.setItems(allAppointments);
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentOfficeColumn.setCellValueFactory(new PropertyValueFactory<>("office"));
        appointmentContactNameColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDateString"));
        appointmentStartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        appointmentEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDateString"));
        appointmentEndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }
}
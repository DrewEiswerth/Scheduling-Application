package org.SchedulingApplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.SchedulingApplication.Utilities.DBConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LoginController implements Initializable {

    private static final Connection connection = DBConnector.getConnection();

    private static String currentUserID;
    private static String currentUserName;
    private static boolean frenchDefault = false;

    public static String getCurrentUserID() {
        return currentUserID;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    @FXML
    private Label titleLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private HBox loginSpacer;

    @FXML
    private Label zoneIDLabel;

    @FXML
    private Button exitButton;

    @FXML
    public void onActionExit() {
        Platform.exit();
    }

    @FXML
    public void onActionLogin(ActionEvent event) throws IOException {

        String enteredUsername = usernameTextField.getText();
        String enteredPassword = passwordTextField.getText();
        boolean correctNameAndPassword = false;
        String loginResult = "FAILURE";
        boolean appWithin15Min = false;
        String appointmentID = null;
        String appStartString = null;

        // queries database for user entity with the entered username and password
        String userSQL = "SELECT user_id, user_name, password " +
                         "FROM users " +
                         "WHERE user_name = ? AND password = ?;";

        // logs the login attempt in the database
        String loginSQL = "INSERT INTO login_activity " +
                          "VALUES(default, ?, ?, NOW(), ?);";

        // if username/password found, query database for all appointments associated with that user
        String appointmentsSQL = "SELECT appointment_id, start " +
                                 "FROM appointments " +
                                 "WHERE user_id = ?;";

        try (PreparedStatement userStatement = connection.prepareStatement(userSQL);
             PreparedStatement loginStatement = connection.prepareStatement(loginSQL);
             PreparedStatement appointmentsStatement = connection.prepareStatement(appointmentsSQL)) {

            userStatement.setString(1, enteredUsername);
            userStatement.setString(2, enteredPassword);

            try (ResultSet userResults = userStatement.executeQuery()) {

                while (userResults.next()) {
                    String retrievedUserID = userResults.getString("user_id");
                    String retrievedUserName = userResults.getString("user_name");
                    String retrievedPassword = userResults.getString("password");

                    // ensures case sensitivity
                    if (enteredUsername.equals(retrievedUserName) && enteredPassword.equals(retrievedPassword)) {
                        correctNameAndPassword = true;
                        currentUserID = retrievedUserID;
                        currentUserName = retrievedUserName;
                        loginResult = "SUCCESS";
                        break;
                    }
                }
            }
            catch (SQLException se2) {
                se2.printStackTrace();
            }

            loginStatement.setString(1, enteredUsername);
            loginStatement.setString(2, enteredPassword);
            loginStatement.setString(3, loginResult);
            loginStatement.execute();

            // if username/password not in database, show alert and return
            if(!correctNameAndPassword) {
                usernameTextField.clear();
                passwordTextField.clear();
                String message;

                if(frenchDefault) {
                    message = "Le nom d'utilisateur et/ou\nle mot de passe est incorrect.";
                }
                else {
                    message = "Username and/or Password is incorrect.";
                }
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(message);
                alert.showAndWait();
                return;
            }

            appointmentsStatement.setString(1, currentUserID);

            try (ResultSet appointmentsResults = appointmentsStatement.executeQuery()) {

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime currentPlus15 = currentTime.plusMinutes(15);

                while (appointmentsResults.next()) {
                    Timestamp startTimestamp = appointmentsResults.getTimestamp("start");
                    LocalDateTime appStartTime = startTimestamp.toLocalDateTime();

                    // checks if appointment is within 15 minutes of current time
                    if ((appStartTime.isAfter(currentTime) || appStartTime.isEqual(currentTime))
                            && (appStartTime.isBefore(currentPlus15) || appStartTime.isEqual(currentPlus15))) {

                        appWithin15Min = true;
                        appointmentID = appointmentsResults.getString("appointment_id");
                        appStartString = dtf.format(appStartTime);
                        break;
                    }
                }
            }
            catch (SQLException se3) {
                se3.printStackTrace();
            }
        }
        catch (SQLException se1) {
            se1.printStackTrace();
        }

        if(appWithin15Min) {
            Alert alert;
            if(frenchDefault) {
                alert = new Alert(Alert.AlertType.WARNING, "Rendez-vous à venir\nRendez-vous identifiant: " +
                        appointmentID + "\nTemps: " + appStartString);

                alert.setTitle("AVERTISSEMENT DE RENDEZ-VOUS");
            }
            else {
                alert = new Alert(Alert.AlertType.WARNING, "Upcoming Appointment\nAppointment ID: " +
                        appointmentID + "\nTime: " + appStartString);

                alert.setTitle("APPOINTMENT WARNING");
            }
            alert.showAndWait();
        }

//        (StageSetter.setStage(event, "org/example/MainScreen.fxml", "Main Menu")).show();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Main Screen");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFormatter<String> usernameFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        TextFormatter<String> passwordFormatter = new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 50) {
                change.setText("");
            }
            return change;
        });

        usernameTextField.setTextFormatter(usernameFormatter);
        passwordTextField.setTextFormatter(passwordFormatter);

        zoneIDLabel.setText(TimeZone.getDefault().getID());

        if(Locale.getDefault().getLanguage().equals("fr")) {
            frenchDefault = true;
            titleLabel.setText("Connexion à une Application de Planification");
            usernameLabel.setPrefWidth(137.6);
            usernameLabel.setText("Nom d'utilisateur:");
            passwordLabel.setPrefWidth(137.6);
            passwordLabel.setText("Mot de passe:");
            loginButton.setPrefWidth(102);
            loginSpacer.setPrefWidth(77.6);
            loginButton.setText("CONNEXION");
            exitButton.setText("SORTIE");
        }
    }
}

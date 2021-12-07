package org.SchedulingApplication.Utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.SchedulingApplication.Model.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DBAppointmentGetter {

    public static ObservableList<Appointment> getAllAppointments() {

        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String sql = "SELECT appointment_id, title, type, description, office, start, end, contact_name, customer_id, contact_id, user_id " +
                     "FROM appointments JOIN contacts USING (contact_id) JOIN users USING (user_id) " +
                     "ORDER BY appointment_id ASC;";

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                int appointmentID = results.getInt("appointment_id");
                String title = results.getString("title");
                String type = results.getString("type");
                String description = results.getString("description");
                String office = results.getString("office");

                Timestamp startDateTimestamp = results.getTimestamp("start");
                String startDateString = dateFormat.format(startDateTimestamp);
                String startTimeString = timeFormat.format(startDateTimestamp);

                Timestamp endDateTimestamp = results.getTimestamp("end");
                String endDateString = dateFormat.format(endDateTimestamp);
                String endTimeString = timeFormat.format(endDateTimestamp);

                int customerID = results.getInt("customer_id");
                String contactName = results.getString("contact_name");
                int contactID = results.getInt("contact_id");
                int userID = results.getInt("user_id");

                appointmentList.add(new Appointment(appointmentID, title, description, office, type, startDateString,
                        startTimeString, endDateString, endTimeString, customerID, contactName, contactID, userID));
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        return appointmentList;
    }
}
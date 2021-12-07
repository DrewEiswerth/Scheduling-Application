package org.SchedulingApplication.Utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.SchedulingApplication.Model.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBCustomerGetter {

    public static ObservableList<Customer> getAllCustomers() {

        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        String sql = "SELECT customer_id, customer_name, address, postal_code, phone, email, country_name, division_name " +
                     "FROM customers JOIN divisions USING(division_id) JOIN countries USING(country_id)" +
                     "ORDER BY customer_id ASC;";

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                int customerID = results.getInt("customer_id");
                String name = results.getString("customer_name");
                String address = results.getString("address");
                String postalCode = results.getString("postal_code");
                String phone = results.getString("phone");
                String email = results.getString("email");
                String countryName = results.getString("country_name");
                String divisionName = results.getString("division_name");

                customerList.add(new Customer(customerID, name, address, postalCode, phone, email, countryName, divisionName));
            }
        }
        catch(SQLException se) {
            se.printStackTrace();
        }

        return customerList;
    }


}

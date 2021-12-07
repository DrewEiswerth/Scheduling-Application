package org.SchedulingApplication.Utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBCustomerCounter {

    public static String getCount(String divisionID) {

        String count = null;
        String sql;

        if(divisionID == null){
            sql =   "SELECT COUNT(customer_id) AS count " +
                    "FROM customers;";
        }
        else {
            sql =   "SELECT COUNT(customer_id) AS count " +
                    "FROM customers " +
                    "WHERE division_id = " + divisionID + ";";
        }

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                count = results.getString("count");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        return count;
    }

    public static String getCountByCountry(String countryID) {

        String count = null;

        String sql = "SELECT COUNT(customer_id) as count " +
                     "FROM customers JOIN divisions USING(division_id) " +
                     "WHERE country_id = ?;";

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql)) {

            statement.setString(1, countryID);

            try (ResultSet results = statement.executeQuery()) {

                while (results.next()) {
                    count = results.getString("count");
                }
            }
            catch (SQLException se2) {
                se2.printStackTrace();
            }
        }
        catch (SQLException se1) {
            se1.printStackTrace();
        }

        return count;
    }
}

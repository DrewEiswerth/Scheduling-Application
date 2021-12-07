package org.SchedulingApplication.Utilities;

//import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    public static Connection connection = null;

    public static void openConnection() {

        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/appointments_database",
                                                    "root",
                                                "NewPassword1!");

            if(connection != null) {
                System.out.println("Database connection successfully established.");
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {

        try {
            connection.close();

            if(connection.isClosed()) {
                System.out.println("Database connection closed.");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}

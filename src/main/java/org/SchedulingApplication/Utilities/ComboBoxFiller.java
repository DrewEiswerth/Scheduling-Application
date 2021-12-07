package org.SchedulingApplication.Utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.SchedulingApplication.Model.ComboBoxItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComboBoxFiller {

    public static ObservableList<ComboBoxItem> requestGenericList(String tableName, String nameAttribute,
                                                                  String idAttribute, String countryID,
                                                                  boolean allOption, boolean displayByID) {

        ObservableList<ComboBoxItem> requestedList = FXCollections.observableArrayList();

        if(allOption) {
            requestedList.add(new ComboBoxItem("ALL", -1, false));
        }

        String sql;

        if(countryID == null) {
            sql =   "SELECT DISTINCT " + nameAttribute + ", " + idAttribute +
                    " FROM " + tableName + ";";
        }
        else {
            sql =   "SELECT DISTINCT " + nameAttribute + ", " + idAttribute +
                    " FROM " + tableName +
                    " WHERE country_id = " + countryID + ";";
        }

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while(results.next()) {
                String name = results.getString(nameAttribute);
                int id = results.getInt(idAttribute);
                requestedList.add(new ComboBoxItem(name, id, displayByID));
            }
        }
        catch(SQLException se) {
            se.printStackTrace();
        }

        return requestedList;
    }

    public static ObservableList<ComboBoxItem> requestTypeList() {

        ObservableList<ComboBoxItem> typeList = FXCollections.observableArrayList();

        typeList.add(new ComboBoxItem("PHONE", 1, false));
        typeList.add(new ComboBoxItem("ZOOM", 2, false));
        typeList.add(new ComboBoxItem("IN-PERSON", 3, false));

        return typeList;
    }

    public static ObservableList<ComboBoxItem> requestOfficeList(boolean allOption) {

        ObservableList<ComboBoxItem> officeList = FXCollections.observableArrayList();

        if(allOption) {
            officeList.add(new ComboBoxItem("ALL", -1, false));
        }

        officeList.add(new ComboBoxItem("DENVER", 1, false));
        officeList.add(new ComboBoxItem("MONTREAL", 2, false));
        officeList.add(new ComboBoxItem("LONDON", 3, false));

        return  officeList;
    }

    public static ObservableList<ComboBoxItem> requestYearList() {

        ObservableList<ComboBoxItem> yearList = FXCollections.observableArrayList();

        yearList.add(new ComboBoxItem("ALL", -1, false));

        String sql = "SELECT DISTINCT YEAR(start) as year " +
                     "FROM appointments;";

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            int count = 0 ;

            while(results.next()) {
                count++;
                String year = results.getString("year");

                yearList.add(new ComboBoxItem(year, count, false));
            }
        }
        catch(SQLException se) {
            se.printStackTrace();
        }

        return yearList;
    }

    public static ObservableList<ComboBoxItem> requestMonthList() {

        ObservableList<ComboBoxItem> monthList = FXCollections.observableArrayList();

        monthList.add(new ComboBoxItem("ALL", -1, false));
        monthList.add(new ComboBoxItem("January", 1, false));
        monthList.add(new ComboBoxItem("February", 2, false));
        monthList.add(new ComboBoxItem("March", 3, false));
        monthList.add(new ComboBoxItem("April", 4, false));
        monthList.add(new ComboBoxItem("May", 5, false));
        monthList.add(new ComboBoxItem("June", 6, false));
        monthList.add(new ComboBoxItem("July", 7, false));
        monthList.add(new ComboBoxItem("August", 8, false));
        monthList.add(new ComboBoxItem("September", 9, false));
        monthList.add(new ComboBoxItem("October", 10, false));
        monthList.add(new ComboBoxItem("November", 11, false));
        monthList.add(new ComboBoxItem("December", 12, false));

        return monthList;
    }

    public static ObservableList<ComboBoxItem> requestWeekList(String month) {

        ObservableList<ComboBoxItem> weekList = FXCollections.observableArrayList();

        weekList.add(new ComboBoxItem("ALL", -1, false));

        if(month.equals("February")) {
            weekList.add(new ComboBoxItem("February 1st - 7th", 7, false));
            weekList.add(new ComboBoxItem("February 8th - 14th", 14, false));
            weekList.add(new ComboBoxItem("February 15th - 21st", 21, false));
            weekList.add(new ComboBoxItem("February 22nd - 28th", 28, false));
            weekList.add(new ComboBoxItem("February 29th", 35, false));
        }
        else if(month.equals("April") || month.equals("June") || month.equals("September") || month.equals("November")) {
            weekList.add(new ComboBoxItem(month + " 1st - 7th", 7, false));
            weekList.add(new ComboBoxItem(month + " 8th - 14th", 14, false));
            weekList.add(new ComboBoxItem(month + " 15th - 21st", 21, false));
            weekList.add(new ComboBoxItem(month + " 22nd - 28th", 28, false));
            weekList.add(new ComboBoxItem(month + " 29th - 30th", 35, false));
        }
        else {
            weekList.add(new ComboBoxItem(month + " 1st - 7th", 7, false));
            weekList.add(new ComboBoxItem(month + " 8th - 14th", 14, false));
            weekList.add(new ComboBoxItem(month + " 15th - 21st", 21, false));
            weekList.add(new ComboBoxItem(month + " 22nd - 28th", 28, false));
            weekList.add(new ComboBoxItem(month + " 29th - 31th", 35, false));
        }

        return weekList;    }
}

package org.SchedulingApplication.Utilities;

import javafx.collections.ObservableList;
import org.SchedulingApplication.Model.Appointment;

public class ReportsFiller {

    public static int[][] retrieveGraphValues(String year, String office, String contactID) {

        ObservableList<Appointment> filteredAppointments = DBAppointmentGetter.getAllAppointments();

        if(year != null) {
            filteredAppointments.removeIf(appointment -> !appointment.getStartDateString().substring(6, 10).equals(year));
        }
        if(office != null) {
            filteredAppointments.removeIf(appointment -> !appointment.getOffice().equals(office));
        }
        if(contactID != null) {
            filteredAppointments.removeIf(appointment -> !String.valueOf(appointment.getContactID()).equals(contactID));
        }

        int[][] graphValuesList = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},   // PHONE
                                   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},   // ZOOM
                                   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};  // IN-PERSON

        for(Appointment appointment : filteredAppointments) {
            String startMonth = appointment.getStartDateString().substring(0, 2);
            String type = appointment.getType();

            if(type.equals("PHONE")) {
                switch (startMonth) {
                    case "01":
                        graphValuesList[0][0]++;
                        break;
                    case "02":
                        graphValuesList[0][1]++;
                        break;
                    case "03":
                        graphValuesList[0][2]++;
                        break;
                    case "04":
                        graphValuesList[0][3]++;
                        break;
                    case "05":
                        graphValuesList[0][4]++;
                        break;
                    case "06":
                        graphValuesList[0][5]++;
                        break;
                    case "07":
                        graphValuesList[0][6]++;
                        break;
                    case "08":
                        graphValuesList[0][7]++;
                        break;
                    case "09":
                        graphValuesList[0][8]++;
                        break;
                    case "10":
                        graphValuesList[0][9]++;
                        break;
                    case "11":
                        graphValuesList[0][10]++;
                        break;
                    case "12":
                        graphValuesList[0][11]++;
                        break;
                }
            }
            else if(type.equals("ZOOM")) {
                switch (startMonth) {
                    case "01":
                        graphValuesList[1][0]++;
                        break;
                    case "02":
                        graphValuesList[1][1]++;
                        break;
                    case "03":
                        graphValuesList[1][2]++;
                        break;
                    case "04":
                        graphValuesList[1][3]++;
                        break;
                    case "05":
                        graphValuesList[1][4]++;
                        break;
                    case "06":
                        graphValuesList[1][5]++;
                        break;
                    case "07":
                        graphValuesList[1][6]++;
                        break;
                    case "08":
                        graphValuesList[1][7]++;
                        break;
                    case "09":
                        graphValuesList[1][8]++;
                        break;
                    case "10":
                        graphValuesList[1][9]++;
                        break;
                    case "11":
                        graphValuesList[1][10]++;
                        break;
                    case "12":
                        graphValuesList[1][11]++;
                        break;
                }
            }
            else {  // IN-PERSON
                switch (startMonth) {
                    case "01":
                        graphValuesList[2][0]++;
                        break;
                    case "02":
                        graphValuesList[2][1]++;
                        break;
                    case "03":
                        graphValuesList[2][2]++;
                        break;
                    case "04":
                        graphValuesList[2][3]++;
                        break;
                    case "05":
                        graphValuesList[2][4]++;
                        break;
                    case "06":
                        graphValuesList[2][5]++;
                        break;
                    case "07":
                        graphValuesList[2][6]++;
                        break;
                    case "08":
                        graphValuesList[2][7]++;
                        break;
                    case "09":
                        graphValuesList[2][8]++;
                        break;
                    case "10":
                        graphValuesList[2][9]++;
                        break;
                    case "11":
                        graphValuesList[2][10]++;
                        break;
                    case "12":
                        graphValuesList[2][11]++;
                        break;
                }
            }
        }
        return graphValuesList;
    }
}


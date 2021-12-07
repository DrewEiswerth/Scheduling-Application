package org.SchedulingApplication.Utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;

public class AppointmentTimeValidator {

    public static boolean checkIfNotWithinBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime, String office) {

        ZoneId localZone = ZoneId.systemDefault();
        ZoneId officeZone;

        if(office.equals("DENVER")) {
            officeZone = ZoneId.of("America/Denver");
        }
        else if(office.equals("MONTREAL")) {
            officeZone = ZoneId.of("Canada/Eastern");
        }
        else {
            officeZone = ZoneId.of("Europe/London");
        }

        // converts the proposed start LocalDateTime to the office's timezone
        ZonedDateTime localZonedStart = startDateTime.atZone(localZone);                  // zones start LocalDateTime
        ZonedDateTime officeZonedStart = localZonedStart.withZoneSameInstant(officeZone); // converts to office's timezone

        LocalDateTime officeStartLDT = officeZonedStart.toLocalDateTime();
        LocalDate officeStartDate =  officeStartLDT.toLocalDate();
        LocalTime officeStartTime = officeStartLDT.toLocalTime();

        // converts the proposed end LocalDateTime to the office's timezone
        ZonedDateTime localZonedEnd = endDateTime.atZone(localZone);                      // zones end LocalDateTime
        ZonedDateTime officeZonedEnd = localZonedEnd.withZoneSameInstant(officeZone);     // converts to office's timezone

        LocalDateTime officeEndLDT = officeZonedEnd.toLocalDateTime();
        LocalDate officeEndDate =  officeEndLDT.toLocalDate();
        LocalTime officeEndTime= officeEndLDT.toLocalTime();

        // ensures appointment starts and ends on same day in office timezone
        if(!officeStartDate.isEqual(officeEndDate)) {
            return true;
        }

        // ensures appointment starts and ends between 8:00am and 10:00pm in office timezone
        return officeStartTime.isBefore(LocalTime.of(8, 0)) || officeEndTime.isAfter(LocalTime.of(22, 0));
    }

    public static boolean checkIfOverlappingAppointment(LocalDateTime proposedStartDateTime, LocalDateTime proposedEndDateTime,
                                                        String customerID, String contactID, String appointmentIDToExclude) {

        // starting false ensures that if no appointments for that customer exist, will return false
        boolean hasOverlap = false;

        String sql;

        // when updating an appointment, this makes sure it doesn't check against itself
        if(appointmentIDToExclude == null) {
            sql =   "SELECT start, end " +
                    "FROM appointments " +
                    "WHERE customer_id = " + customerID + " OR contact_id = " +  contactID + ";";
        }
        else {
            sql =   "SELECT start, end " +
                    "FROM appointments " +
                    "WHERE (customer_id = " + customerID + " OR contact_id = " +  contactID + ") " +
                    "AND NOT appointment_id = " + appointmentIDToExclude + ";";
        }

        try (PreparedStatement statement = DBConnector.getConnection().prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while(results.next()) {

                // assumes overlap occurs until proven false
                hasOverlap = true;

                Timestamp startTimestampToCheck = results.getTimestamp("start");
                LocalDateTime scheduledStartDateTime = startTimestampToCheck.toLocalDateTime();

                Timestamp endTimestampToCheck = results.getTimestamp("end");
                LocalDateTime scheduledEndDateTime = endTimestampToCheck.toLocalDateTime();

                // overlap if proposed start or end occurs within another scheduled appointment
                if(((proposedStartDateTime.isAfter(scheduledStartDateTime)) && (proposedStartDateTime.isBefore(scheduledEndDateTime)))
                        || ((proposedEndDateTime.isAfter(scheduledStartDateTime)) && (proposedEndDateTime.isBefore(scheduledEndDateTime)))) {
                    break;
                }
                // overlap if proposed start is the same as another appointment's start, or end is same as another appointment's end
                else if((proposedStartDateTime.isEqual(scheduledStartDateTime)) || (proposedEndDateTime.isEqual(scheduledEndDateTime))) {
                    break;
                }
                // overlap if proposed appointment would fully encompass another appointment
                else if((proposedStartDateTime.isBefore(scheduledStartDateTime)) && (proposedEndDateTime.isAfter(scheduledEndDateTime))) {
                    break;
                }
                // else no overlap, if this is last appointment to check for customer
                else {
                    hasOverlap = false;
                }
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        return hasOverlap;
    }
}

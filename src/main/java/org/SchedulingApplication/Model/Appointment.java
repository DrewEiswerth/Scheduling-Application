package org.SchedulingApplication.Model;

public class Appointment {

    private final int appointmentID;
    private final String title;
    private final String description;
    private final String office;
    private final String type;
    private final String startDateString;
    private final String startTimeString;
    private final String endDateString;
    private final String endTimeString;
    private final int customerID;
    private final String contactName;
    private final int contactID;
    private final int userID;

    public Appointment(int appointmentID, String title, String description, String office, String type,
                       String startDateString, String startTimeString, String endDateString, String endTimeString,
                       int customerID, String contactName, int contactID, int userID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.office = office;
        this.type = type;
        this.startDateString = startDateString;
        this.startTimeString = startTimeString;
        this.endDateString = endDateString;
        this.endTimeString = endTimeString;
        this.customerID = customerID;
        this.contactName = contactName;
        this.contactID = contactID;
        this.userID = userID;
    }

    public int getAppointmentID() {
        return appointmentID;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getOffice() {
        return office;
    }
    public String getType() {
        return type;
    }
    public String getStartDateString() {
        return startDateString;
    }
    public String getStartTimeString() {
        return startTimeString;
    }
    public String getEndDateString() {
        return endDateString;
    }
    public String getEndTimeString() {
        return endTimeString;
    }
    public int getCustomerID() {
        return customerID;
    }
    public String getContactName() {
        return contactName;
    }
    public int getContactID() {
        return contactID;
    }
    public int getUserID() {
        return userID;
    }
}

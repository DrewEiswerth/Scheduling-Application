package org.SchedulingApplication.Model;

public class Customer {

    private final int customerID;
    private final String name;
    private final String address;
    private final String postalCode;
    private final String phone;
    private final String email;
    private final String countryName;
    private final String divisionName;

    public Customer(int customerID, String name, String address, String postalCode, String phone,
                    String email, String countryName, String divisionName) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.email = email;
        this.countryName = countryName;
        this.divisionName = divisionName;
    }

    public int getCustomerID() {
        return customerID;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }
    public String getCountryName() {
        return countryName;
    }
    public String getDivisionName() {
        return divisionName;
    }
}

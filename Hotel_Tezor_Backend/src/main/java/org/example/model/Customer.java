package org.example.model;

/**
 * Customer: Plain data class representing a hotel customer.
 * Contains customer details like ID, code, name, phone, and email.
 */
public class Customer {
    private int customerId;
    private String customerCode;
    private String name;
    private String phone;
    private String email;

    /**
     * Constructor to create a Customer instance.
     */
    public Customer(int customerId, String customerCode, String name, String phone, String email) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}

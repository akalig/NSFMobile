package com.mycompany.nsfmobile.adapters;

public class BookingData {

    private String bookingNo;
    private String pono;
    private String customerName;

    public BookingData(String bookingNo, String pono, String customerName) {
        this.bookingNo = bookingNo;
        this.pono = pono;
        this.customerName = customerName;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public String getPono() {
        return pono;
    }

    public String getCustomerName() {
        return customerName;
    }
}
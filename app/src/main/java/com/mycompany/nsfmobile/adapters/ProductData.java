package com.mycompany.nsfmobile.adapters;

public class ProductData {

    private String id;
    private String productId;
    private String description;
    private String bookingNo;
    private String price;
    private String quantity;
    private String size;

    public ProductData(String id, String productId, String description, String bookingNo, String price, String quantity, String size) {
        this.id = id;
        this.productId = productId;
        this.description = description;
        this.bookingNo = bookingNo;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSize() { return size; }
}

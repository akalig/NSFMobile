package com.mycompany.nsfmobile.adapters;

public class Product {
    private String description;
    private String type;
    private String quantity;
    private String noofbox;
    private String price;
    private String productid;

    public Product(String description, String type, String quantity, String noofbox, String price, String productid) {
        this.description = description;
        this.type = type;
        this.quantity = quantity;
        this.noofbox = noofbox;
        this.price = price;
        this.productid = productid;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getNoofbox() {
        return noofbox;
    }

    public String getPrice() {
        return price;
    }

    public String getProductid() {
        return productid;
    }


}

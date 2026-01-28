package ProductStuff;

import java.util.UUID;

public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private int sales;

    // Constructor for a NEW product (Dynamic Logic: Sales start at 0)
    public Product(String name, double price, int quantity) {
        this.id = "PROD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.sales = 0;
    }

    // Getters for MongoDB and UI
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getSales() { return sales; }
}
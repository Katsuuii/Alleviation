package ProductStuff;

public class ProductRow {
    private final String id;
    private final String name;
    private final double price;
    private final int quantity;
    private final int sales;

    public ProductRow(String id, String name, double price, int quantity, int sales) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.sales = sales;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getSales() { return sales; }
}

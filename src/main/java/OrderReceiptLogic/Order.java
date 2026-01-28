package OrderReceiptLogic;

import java.util.List;


public class Order {
    final private int id;
    final private int orderNumber;
    final private int userId;
    final private List<OrderLine> orderLines;
    final private String firstName;
    final private String lastName;
    final private String email;

    public Order(int id, int orderNumber, int userId, List<OrderLine> orderLines,
                 String firstName, String lastName, String email) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.orderLines = orderLines;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getID() { return id; }
    public int getOrderNumber() { return orderNumber; }
    public int getUserId() { return userId; }
    public List<OrderLine> getOrderLines() { return orderLines; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    public OrderLine[] getLines() {
        return null;
    }
}

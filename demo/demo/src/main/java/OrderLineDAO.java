import java.util.List;


public interface OrderLineDAO {
    void insert(OrderLineController orderline);
    List<OrderLineController> findAll();
    void delete(String id);
}
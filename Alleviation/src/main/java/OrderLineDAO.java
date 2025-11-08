import java.util.List;


public interface OrderLineDAO {
    void insert(OrderLine orderline);
    List<OrderLine> findAll();
    void delete(String id);
}
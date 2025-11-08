import java.util.List;

public interface WishlistDAO {
    void insert(Wishlist wishlist);
    List<Wishlist> findAll();
    void delete(String id);
}
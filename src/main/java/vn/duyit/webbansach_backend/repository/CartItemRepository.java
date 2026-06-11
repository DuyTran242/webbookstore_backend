package vn.duyit.webbansach_backend.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.duyit.webbansach_backend.entity.CartItem;

import java.util.List;
import java.util.Optional;
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    // Spring Data JPA sẽ tự động hiểu và tạo câu lệnh SQL:
    // DELETE FROM cart_items WHERE user_id = ?
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.cart.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // (Nếu Entity CartItem của bạn khai báo là: private User user;)
    // Thì bạn đổi tên hàm thành: void deleteByUser_Id(Long userId);

}

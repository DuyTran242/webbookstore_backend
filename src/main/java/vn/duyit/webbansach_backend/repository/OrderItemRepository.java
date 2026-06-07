package vn.duyit.webbansach_backend.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.OrderItem;

import java.util.List;
import java.util.Optional;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
    // Spring Data JPA sẽ tự động dịch hàm này thành: SELECT * FROM order_items WHERE serial_id = ?
    // Trả về Optional để xử lý trường hợp không tìm thấy (null) an toàn hơn
    Optional<OrderItem> findBySerialId(Long serialId);

}
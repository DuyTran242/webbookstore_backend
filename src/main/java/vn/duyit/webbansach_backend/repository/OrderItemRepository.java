package vn.duyit.webbansach_backend.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

}
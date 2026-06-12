package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    boolean existsByUserIdAndOrderItems_ProductIdAndStatus(Long userId, Long productId, String status);
    boolean existsByUserIdAndOrderItems_ProductId(Long userId, Long productId);

}
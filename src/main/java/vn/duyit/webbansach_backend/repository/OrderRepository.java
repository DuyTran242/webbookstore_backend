package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.duyit.webbansach_backend.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    boolean existsByUserIdAndOrderItems_ProductIdAndStatus(Long userId, Long productId, String status);
    boolean existsByUserIdAndOrderItems_ProductId(Long userId, Long productId);

    @Query(value = "SELECT SUM(total_price) FROM orders WHERE payment_status = 'PAID'", nativeQuery = true)
    BigDecimal calculateTotalPaidRevenue();

    @Query(value = "SELECT SUM(total_price) FROM orders WHERE payment_status = 'PAID' AND MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())", nativeQuery = true)
    BigDecimal calculateThisMonthPaidRevenue();

    @Query(value = "SELECT SUM(total_price) FROM orders WHERE payment_status = 'PAID' AND MONTH(created_at) = MONTH(CURDATE() - INTERVAL 1 MONTH) AND YEAR(created_at) = YEAR(CURDATE() - INTERVAL 1 MONTH)", nativeQuery = true)
    BigDecimal calculateLastMonthPaidRevenue();

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status != 'Đã hủy'", nativeQuery = true)
    long countTotalValidOrders();

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status != 'Đã hủy' AND MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())", nativeQuery = true)
    long countThisMonthValidOrders();

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = 'PENDING'", nativeQuery = true)
    long countPendingOrders();

    @Query(value = "SELECT COUNT(*) FROM orders WHERE status = 'Đang giao'", nativeQuery = true)
    long countDeliveringOrders();

    @Query(value = "SELECT DATE(created_at) as date, SUM(total_price) as revenue, COUNT(*) as count FROM orders WHERE payment_status = 'PAID' AND created_at >= DATE(NOW()) - INTERVAL 30 DAY GROUP BY DATE(created_at) ORDER BY DATE(created_at) ASC", nativeQuery = true)
    List<Object[]> getDailyRevenueAndOrdersLast30Days();

    @Query(value = "SELECT DATE_FORMAT(created_at, '%m/%Y') as month, SUM(total_price) as revenue, COUNT(*) as count FROM orders WHERE payment_status = 'PAID' AND created_at >= DATE(NOW()) - INTERVAL 12 MONTH GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY DATE_FORMAT(created_at, '%Y-%m') ASC", nativeQuery = true)
    List<Object[]> getMonthlyRevenueAndOrdersLast12Months();

}
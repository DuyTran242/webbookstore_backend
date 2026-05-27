package vn.duyit.webbansach_backend.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.duyit.webbansach_backend.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Order, Long> {

    // Tổng doanh thu (đơn PAID)
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal totalRevenue();

    // Doanh thu theo từng ngày trong khoảng thời gian
    @Query("SELECT DATE(o.createdAt), COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o WHERE o.paymentStatus = 'PAID' " +
            "AND o.createdAt BETWEEN :from AND :to " +
            "GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> revenueByDay(@Param("from") LocalDateTime from,
                                @Param("to")   LocalDateTime to);

    // Doanh thu theo từng tháng trong năm
    @Query("SELECT MONTH(o.createdAt), COALESCE(SUM(o.totalPrice), 0) " +
            "FROM Order o WHERE o.paymentStatus = 'PAID' " +
            "AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) ORDER BY MONTH(o.createdAt)")
    List<Object[]> revenueByMonth(@Param("year") int year);

    // Doanh thu tháng này
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.paymentStatus = 'PAID' " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    BigDecimal revenueThisMonth();

    // Doanh thu tháng trước
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.paymentStatus = 'PAID' " +
            "AND o.createdAt >= :from AND o.createdAt < :to")
    BigDecimal revenueBetween(@Param("from") LocalDateTime from,
                              @Param("to")   LocalDateTime to);


    // Đếm đơn theo trạng thái
    long countByStatus(String status);

    // Đơn hàng theo ngày
    @Query("SELECT DATE(o.createdAt), COUNT(o) " +
            "FROM Order o WHERE o.createdAt BETWEEN :from AND :to " +
            "GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> orderCountByDay(@Param("from") LocalDateTime from,
                                   @Param("to")   LocalDateTime to);

    // Đơn hàng trong tháng hiện tại
    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    long countThisMonth();

    //Top sách bán chạy
    @Query("SELECT oi.product.id, oi.product.name, oi.product.brand, " +
            "SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o WHERE o.paymentStatus = 'PAID' " +
            "AND (:from IS NULL OR o.createdAt >= :from) " +
            "GROUP BY oi.product.id, oi.product.name, oi.product.brand " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> topSellingBooks(@Param("from") LocalDateTime from,
                                   org.springframework.data.domain.Pageable pageable);

    //Top danh mục
    @Query("SELECT oi.product.category.name, SUM(oi.quantity), " +
            "SUM(oi.price * oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o WHERE o.paymentStatus = 'PAID' " +
            "AND oi.product.category IS NOT NULL " +
            "GROUP BY oi.product.category.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> topCategories(org.springframework.data.domain.Pageable pageable);

    // Người dùng mới theo ngày
    @Query(value = "SELECT DATE(created_at), COUNT(*) FROM users " +
            "WHERE created_at BETWEEN :from AND :to " +
            "GROUP BY DATE(created_at) ORDER BY DATE(created_at)",
            nativeQuery = true)
    List<Object[]> newUsersByDay(@Param("from") LocalDateTime from,
                                 @Param("to")   LocalDateTime to);

    // Tổng số user
    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    long totalUsers();

    // User mới tháng này
    @Query(value = "SELECT COUNT(*) FROM users " +
            "WHERE YEAR(created_at) = YEAR(CURDATE()) " +
            "AND MONTH(created_at) = MONTH(CURDATE())",
            nativeQuery = true)
    long newUsersThisMonth();
}
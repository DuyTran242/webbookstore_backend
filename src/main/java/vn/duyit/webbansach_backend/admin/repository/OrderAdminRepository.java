package vn.duyit.webbansach_backend.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.duyit.webbansach_backend.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderAdminRepository extends JpaRepository<Order, Long> {

    // Tìm kiếm + lọc đơn hàng (phân trang)
    @Query("SELECT o FROM Order o WHERE " +
            "(:keyword IS NULL OR " +
            "   LOWER(o.phone) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "   CAST(o.id AS string) LIKE CONCAT('%',:keyword,'%') OR " +
            "   (o.user IS NOT NULL AND LOWER(o.user.fullName) LIKE LOWER(CONCAT('%',:keyword,'%'))) OR " +
            "   (o.user IS NOT NULL AND LOWER(o.user.email) LIKE LOWER(CONCAT('%',:keyword,'%')))) " +
            "AND (:status    IS NULL OR o.status = :status) " +
            "AND (:payStatus IS NULL OR o.paymentStatus = :payStatus) " +
            "AND (:fromDate  IS NULL OR o.createdAt >= :fromDate) " +
            "AND (:toDate    IS NULL OR o.createdAt <= :toDate)")
    Page<Order> searchOrders(
            @Param("keyword")   String keyword,
            @Param("status")    String status,
            @Param("payStatus") String payStatus,
            @Param("fromDate")  LocalDateTime fromDate,
            @Param("toDate")    LocalDateTime toDate,
            Pageable pageable
    );

    // Đếm theo trạng thái (cho thẻ thống kê)
    long countByStatus(String status);

    // Đếm theo trạng thái thanh toán
    long countByPaymentStatus(String paymentStatus);

    // Tổng doanh thu từ đơn đã thanh toán
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    java.math.BigDecimal sumRevenuePaid();

    // Doanh thu theo ngày (7 ngày gần nhất, dùng cho chart)
    @Query("SELECT DATE(o.createdAt) as day, COALESCE(SUM(o.totalPrice),0) as revenue " +
            "FROM Order o WHERE o.paymentStatus = 'PAID' " +
            "AND o.createdAt >= :from GROUP BY DATE(o.createdAt) ORDER BY day")
    List<Object[]> revenueByDay(@Param("from") LocalDateTime from);
}
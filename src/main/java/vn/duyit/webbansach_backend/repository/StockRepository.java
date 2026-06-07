package vn.duyit.webbansach_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.duyit.webbansach_backend.entity.Product;

public interface StockRepository extends JpaRepository<Product, Long> {

    // Tìm kiếm + lọc theo trạng thái tồn kho
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:stockStatus = 'all' OR " +
            "    (:stockStatus = 'out' AND p.stockQuantity = 0) OR " +
            "    (:stockStatus = 'low' AND p.stockQuantity > 0 AND p.stockQuantity <= 10) OR " +
            "    (:stockStatus = 'ok'  AND p.stockQuantity > 10))")
    Page<Product> findByStockFilter(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("stockStatus") String stockStatus,
            Pageable pageable
    );

    // Đếm tổng số sách hết hàng
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity = 0")
    long countOutOfStock();

    // Đếm tổng số sách sắp hết (1–10)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity > 0 AND p.stockQuantity <= 10")
    long countLowStock();

    // Đếm tổng số sách còn hàng (> 10)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity > 10")
    long countInStock();

    // Tổng số lượng tồn kho toàn bộ
    @Query("SELECT COALESCE(SUM(p.stockQuantity), 0) FROM Product p")
    long sumTotalStock();
}
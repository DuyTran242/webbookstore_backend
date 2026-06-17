package vn.duyit.webbansach_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContaining(String keyword);
    // THÊM HÀM NÀY ĐỂ PHÂN TRANG:
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    // BỔ SUNG HÀM NÀY ĐỂ TÌM THEO BRAND VÀ PHÂN TRANG:
    Page<Product> findByBrand(String brand, Pageable pageable);
    // Kế thừa sẵn hàm findAll(Pageable pageable) từ JpaRepository
    // Bạn có thể thêm hàm tìm kiếm theo tên nếu muốn:
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    // Spring Data JPA sẽ tự động build câu query: SELECT * FROM products WHERE has_serial = true
    List<Product> findByHasSerialTrue();

    @Query(value = "SELECT COUNT(*) FROM products WHERE stock_quantity > 0", nativeQuery = true)
    long countInStockProducts();

    // THÊM MỚI: Phân trang + tìm kiếm theo tên hoặc brand
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
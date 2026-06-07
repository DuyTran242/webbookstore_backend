package vn.duyit.webbansach_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.Product;

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

}
package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.ProductImage;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);

    // THÊM HÀM NÀY: Tìm ảnh chính (isPrimary = 1) của sản phẩm
    ProductImage findFirstByProductIdAndIsPrimary(Long productId, Integer isPrimary);
    void deleteByProductId(Long productId);
}
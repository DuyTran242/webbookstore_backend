package vn.duyit.webbansach_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.entity.ProductImage;
import vn.duyit.webbansach_backend.repository.ProductImageRepository;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    // Hàm trả về chuỗi Base64 của ảnh chính
    public String getPrimaryImageUrlByProductId(Long productId) {
        ProductImage primaryImage = productImageRepository.findFirstByProductIdAndIsPrimary(productId, 1);
        if (primaryImage != null) {
            return primaryImage.getImageUrl();
        }
        return null; // Trả về null nếu không có ảnh
    }
}
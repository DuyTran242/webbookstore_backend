package vn.duyit.webbansach_backend.orderinface;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.ProductInBrandDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.orderinface.BrandAdminService;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.service.ProductImageService;

@Service
@RequiredArgsConstructor
public class BrandAdminServiceImpl implements BrandAdminService {

    private final ProductRepository productRepository;
    private final ProductImageService productImageService; // Sử dụng service lấy ảnh chính đã viết trước đó

    @Override
    public Page<ProductInBrandDTO> getProductsByBrand(String brand, Pageable pageable) {
        // Lấy sản phẩm phân trang từ DB
        Page<Product> productPage = productRepository.findByBrand(brand, pageable);

        // Chuyển đổi Entity sang DTO
        return productPage.map(product -> {
            ProductInBrandDTO dto = new ProductInBrandDTO();
            BeanUtils.copyProperties(product, dto);

            // Lấy tên category nếu có
            if (product.getCategory() != null) {
                dto.setCategoryName(product.getCategory().getName());
            }

            // Lấy ảnh chính
            dto.setPrimaryImage(productImageService.getPrimaryImageUrlByProductId(product.getId()));

            return dto;
        });
    }
}

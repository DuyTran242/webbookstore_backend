package vn.duyit.webbansach_backend.orderinface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.duyit.webbansach_backend.dto.ProductInBrandDTO;

public interface BrandAdminService {
    Page<ProductInBrandDTO> getProductsByBrand(String brand, Pageable pageable);
}
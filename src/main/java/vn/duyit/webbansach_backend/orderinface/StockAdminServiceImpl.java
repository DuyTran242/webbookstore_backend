package vn.duyit.webbansach_backend.orderinface;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.ProductStockDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.orderinface.StockAdminService;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.service.ProductImageService;

@Service
@RequiredArgsConstructor
public class StockAdminServiceImpl implements StockAdminService {

    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    @Override
    public Page<ProductStockDTO> getAllProductStocks(String keyword, Pageable pageable) {
        Page<Product> productPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(product -> {
            ProductStockDTO dto = new ProductStockDTO();
            BeanUtils.copyProperties(product, dto);
            dto.setPrimaryImage(productImageService.getPrimaryImageUrlByProductId(product.getId()));
            return dto;
        });
    }
}
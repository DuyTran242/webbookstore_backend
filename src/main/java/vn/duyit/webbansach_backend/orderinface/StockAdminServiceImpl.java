package vn.duyit.webbansach_backend.orderinface;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.dto.ProductStockDTO;
import vn.duyit.webbansach_backend.dto.StockSummaryDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.repository.StockRepository;
import vn.duyit.webbansach_backend.service.ProductImageService;

@Service
@RequiredArgsConstructor
public class StockAdminServiceImpl implements StockAdminService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ProductImageService productImageService;

    @Override
    public Page<ProductStockDTO> getAllProductStocks(String keyword, Pageable pageable) {
        return getFilteredProductStocks(keyword, null, "all", pageable);
    }

    @Override
    public Page<ProductStockDTO> getFilteredProductStocks(String keyword, Long categoryId, String stockStatus, Pageable pageable) {
        String status = (stockStatus == null || stockStatus.isEmpty()) ? "all" : stockStatus;
        String searchKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();

        Page<Product> productPage = stockRepository.findByStockFilter(searchKeyword, categoryId, status, pageable);

        return productPage.map(product -> {
            ProductStockDTO dto = new ProductStockDTO();
            BeanUtils.copyProperties(product, dto);
            dto.setPrimaryImage(productImageService.getPrimaryImageUrlByProductId(product.getId()));
            if (product.getCategory() != null) {
                dto.setCategoryName(product.getCategory().getName());
                dto.setCategoryId(product.getCategory().getId());
            }
            return dto;
        });
    }

    @Override
    public StockSummaryDTO getStockSummary() {
        StockSummaryDTO summary = new StockSummaryDTO();
        summary.setTotalProducts(productRepository.count());
        summary.setOutOfStock(stockRepository.countOutOfStock());
        summary.setLowStock(stockRepository.countLowStock());
        summary.setInStock(stockRepository.countInStock());
        summary.setTotalQuantity(stockRepository.sumTotalStock());
        return summary;
    }

    @Override
    @Transactional
    public void importStock(vn.duyit.webbansach_backend.dto.StockUpdateDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + dto.getProductId()));
        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        if ("export".equals(dto.getType())) {
            int newStock = currentStock - dto.getQuantity();
            if (newStock < 0) throw new RuntimeException("Số lượng tồn kho không đủ để xuất!");
            product.setStockQuantity(newStock);
        } else {
            product.setStockQuantity(currentStock + dto.getQuantity());
            if (dto.getImportPrice() != null) {
                product.setImportPrice(dto.getImportPrice());
            }
            if (dto.getSupplier() != null && !dto.getSupplier().isEmpty()) {
                product.setSupplier(dto.getSupplier());
            }
        }
        productRepository.save(product);
    }
}
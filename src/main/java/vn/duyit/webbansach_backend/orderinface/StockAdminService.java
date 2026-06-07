package vn.duyit.webbansach_backend.orderinface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.duyit.webbansach_backend.dto.ProductStockDTO;

public interface StockAdminService {
    Page<ProductStockDTO> getAllProductStocks(String keyword, Pageable pageable);
}
package vn.duyit.webbansach_backend.orderinface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.duyit.webbansach_backend.dto.ProductStockDTO;
import vn.duyit.webbansach_backend.dto.StockSummaryDTO;

public interface StockAdminService {
    Page<ProductStockDTO> getAllProductStocks(String keyword, Pageable pageable);

    Page<ProductStockDTO> getFilteredProductStocks(String keyword, Long categoryId, String stockStatus, Pageable pageable);

    StockSummaryDTO getStockSummary();

    void importStock(vn.duyit.webbansach_backend.dto.StockUpdateDTO dto);
}
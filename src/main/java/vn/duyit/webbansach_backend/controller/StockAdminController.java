package vn.duyit.webbansach_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ProductStockDTO;
import vn.duyit.webbansach_backend.dto.StockSummaryDTO;
import vn.duyit.webbansach_backend.dto.StockUpdateDTO;
import vn.duyit.webbansach_backend.orderinface.StockAdminService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stock")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StockAdminController {

    private final StockAdminService stockAdminService;

    @GetMapping
    public ResponseEntity<Page<ProductStockDTO>> getStock(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "all") String stockStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductStockDTO> stockPage = stockAdminService.getFilteredProductStocks(keyword, categoryId, stockStatus, PageRequest.of(page, size));
        return ResponseEntity.ok(stockPage);
    }

    @GetMapping("/summary")
    public ResponseEntity<StockSummaryDTO> getSummary() {
        return ResponseEntity.ok(stockAdminService.getStockSummary());
    }

    @PostMapping("/import")
    public ResponseEntity<?> importStock(@RequestBody StockUpdateDTO dto) {
        try {
            stockAdminService.importStock(dto);
            return ResponseEntity.ok(Map.of("message", "Nhập kho thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
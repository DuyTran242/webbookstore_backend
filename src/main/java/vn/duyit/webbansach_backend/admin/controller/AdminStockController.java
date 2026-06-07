package vn.duyit.webbansach_backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.service.AdminStockService;
import vn.duyit.webbansach_backend.dto.StockDTO;
import vn.duyit.webbansach_backend.dto.StockSummaryDTO;
import vn.duyit.webbansach_backend.dto.StockUpdateDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stock")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminStockController {

    private final AdminStockService adminStockService;


    @GetMapping
    public ResponseEntity<Page<StockDTO>> getStockList(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "15")  int size,
            @RequestParam(required = false)     String keyword,
            @RequestParam(required = false)     Long categoryId,
            @RequestParam(defaultValue = "all") String stockStatus
    ) {
        Page<StockDTO> result = adminStockService.getStockList(
                page, size, keyword, categoryId, stockStatus);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/summary")
    public ResponseEntity<StockSummaryDTO> getSummary() {
        return ResponseEntity.ok(adminStockService.getSummary());
    }


    @PatchMapping("/update")
    public ResponseEntity<?> updateStock(@RequestBody StockUpdateDTO request) {
        try {
            StockDTO updated = adminStockService.updateStock(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật tồn kho thành công",
                    "product", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PatchMapping("/bulk-update")
    public ResponseEntity<?> bulkUpdateStock(
            @RequestBody List<StockUpdateDTO> requests) {
        try {
            int count = adminStockService.bulkAddStock(requests);
            return ResponseEntity.ok(Map.of(
                    "message", "Nhập hàng thành công " + count + " sản phẩm",
                    "successCount", count
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
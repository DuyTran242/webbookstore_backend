package vn.duyit.webbansach_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ProductStockDTO;
import vn.duyit.webbansach_backend.orderinface.StockAdminService;

@RestController
@RequestMapping("/api/admin/stock")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StockAdminController {

    private final StockAdminService stockAdminService;

    @GetMapping
    public ResponseEntity<Page<ProductStockDTO>> getStock(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductStockDTO> stockPage = stockAdminService.getAllProductStocks(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(stockPage);
    }
}
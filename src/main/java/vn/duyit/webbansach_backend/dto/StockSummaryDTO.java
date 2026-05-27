package vn.duyit.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSummaryDTO {
    private long totalProducts;   // Tổng số đầu sách
    private long outOfStock;      // Hết hàng (= 0)
    private long lowStock;        // Sắp hết (1–10)
    private long inStock;         // Còn hàng (> 10)
    private long totalQuantity;   // Tổng số lượng tồn kho
}
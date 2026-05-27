package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Long productId;
    private String productName;
    private String author;        // brand
    private String publisher;     // material
    private String categoryName;
    private Long categoryId;
    private Double price;
    private Integer stockQuantity;
    private String imageUrl;
    private String stockStatus;   // "out" | "low" | "ok"
}
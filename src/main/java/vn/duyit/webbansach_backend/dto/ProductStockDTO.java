package vn.duyit.webbansach_backend.dto;

import lombok.Data;

@Data
public class ProductStockDTO {
    private Long id;
    private String name;
    private String brand;       // tác giả
    private String categoryName; // danh mục
    private Long categoryId;
    private Double price;
    private Integer stockQuantity;
    private String primaryImage;
    private Double importPrice;
    private String supplier;
}

package vn.duyit.webbansach_backend.dto;

import lombok.Data;

@Data
public class ProductStockDTO {
    private Long id;
    private String name;
    private String brand;
    private Double price;
    private Integer stockQuantity;
    private String primaryImage;
}

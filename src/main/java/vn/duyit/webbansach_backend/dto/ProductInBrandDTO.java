package vn.duyit.webbansach_backend.dto;

import lombok.Data;

@Data
public class ProductInBrandDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String categoryName; // Lấy tên danh mục để hiển thị thêm cho rõ
    private String primaryImage; // Chứa Base64 hoặc URL của ảnh chính
}

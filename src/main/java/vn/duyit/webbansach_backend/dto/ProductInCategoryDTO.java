package vn.duyit.webbansach_backend.dto;
import lombok.Data;

@Data
public class ProductInCategoryDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String brand;
    private String primaryImage; // Chứa Base64 hoặc URL của ảnh chính
}

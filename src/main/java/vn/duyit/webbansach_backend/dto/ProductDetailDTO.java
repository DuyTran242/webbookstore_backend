package vn.duyit.webbansach_backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import vn.duyit.webbansach_backend.entity.ProductImage;

@Getter
@Setter
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String categoryName;
    private Long categoryId;
    private String brand;
    private String material;
    private String color;
    private Double weight;
    private List<ProductImage> images;
    // Chứa danh sách các review đã được DTO hóa
    private List<ReviewResponseDTO> reviews;
    private Integer totalReviews;
    private Double averageRating;
}
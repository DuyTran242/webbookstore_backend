package vn.duyit.webbansach_backend.dto;
import java.util.List;
import lombok.Data;
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String brand;
    private String color;
    private String description;
    private String material;
    private Integer stockQuantity;
    private Long categoryId;
    private Double weight;
    private Integer status;
    private List<ProductImageDTO> images;

    private List<ReviewResponseDTO> reviews;
    private Double averageRating;
    private Integer totalReviews;
    // Getters and Setters cho tất cả các field trên
    // ...


}
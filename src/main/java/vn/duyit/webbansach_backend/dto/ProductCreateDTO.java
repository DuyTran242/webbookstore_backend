package vn.duyit.webbansach_backend.dto;
import lombok.Data;
import java.util.List;

@Data
public class ProductCreateDTO {
    private String name;
    private String brand;
    private String color;
    private String description;
    private String material;
    private Double price;
    private Integer stockQuantity;
    private Long categoryId;
    private Integer status;
    private Double weight;

    // Danh sách ảnh đính kèm
    private List<ProductImageDTO> images;
}
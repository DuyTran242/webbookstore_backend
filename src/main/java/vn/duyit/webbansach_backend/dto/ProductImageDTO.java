package vn.duyit.webbansach_backend.dto;
import lombok.Data;

@Data
public class ProductImageDTO {
    private String imageUrl; // Chứa chuỗi Base64
    private Integer isPrimary; // 1: Ảnh chính, 0: Ảnh phụ
}
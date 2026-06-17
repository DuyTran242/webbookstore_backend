package vn.duyit.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về thông tin sản phẩm yêu thích.
 * Chứa các trường cần thiết để hiển thị trên UI.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteProductDto {
    private Long id;
    private String name;
    private Double price;
    private String image; // URL hoặc đường dẫn tới ảnh chính của sản phẩm
}

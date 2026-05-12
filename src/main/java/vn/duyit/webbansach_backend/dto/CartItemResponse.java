package vn.duyit.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long id;            // ID của CartItem (để thao tác xóa/sửa)
    private Long productId;     // ID của Product
    private String productName; // Tên sản phẩm
    private String imageUrl;    // Ảnh sản phẩm
    private Double price;   // Giá tại thời điểm thêm vào giỏ
    private Integer quantity;   // Số lượng
}
// 2. OrderItemDTO.java (Dùng cho danh sách sản phẩm trong Chi tiết đơn)
package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String imageUrl; // Giả sử entity Product của bạn có trường này
    private Integer quantity;
    private BigDecimal price;
}
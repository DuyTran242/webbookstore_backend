package vn.duyit.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    // Thông tin người nhận (Khớp với form Checkout)
    private String fullName;
    private String email;
    private String phone;
    private String note;

    // Địa chỉ chi tiết để lưu vào bảng orders
    private String address;
    private String province;
    private String district;
    private String ward;

    // Thông tin thanh toán và vận chuyển
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;
    private String paymentMethod; // "VNPAY" hoặc "COD"
    private Long userId; // ID người dùng nếu đã đăng nhập

    // Danh sách sản phẩm trong đơn hàng (Khớp bảng order_items)
    private List<OrderItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
    }
}
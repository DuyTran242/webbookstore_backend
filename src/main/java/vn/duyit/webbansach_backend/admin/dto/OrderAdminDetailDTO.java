package vn.duyit.webbansach_backend.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class OrderAdminDetailDTO {
    // Thông tin đơn
    private Long   id;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String note;
    private LocalDateTime createdAt;

    // Thông tin tài chính
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discount;

    // Thông tin khách hàng
    private Long   userId;
    private String customerName;
    private String customerEmail;
    private String phone;
    private String shippingAddress;

    // Thông tin vận chuyển
    private String shippingProvider;
    private String trackingCode;
    private String shippingStatus;

    // Thông tin thanh toán
    private String transactionId;

    // Danh sách sách trong đơn
    private List<OrderAdminItemDTO> items;

    @Data
    public static class OrderAdminItemDTO {
        private Long   productId;
        private String productName;
        private String author;      // brand
        private String imageUrl;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }
}

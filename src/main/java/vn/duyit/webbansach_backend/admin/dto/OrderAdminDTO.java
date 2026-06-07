package vn.duyit.webbansach_backend.admin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ─── DTO danh sách đơn hàng (bảng chính) ────────────────────────────────────
@Data
public class OrderAdminDTO {
    private Long   id;
    private String customerName;
    private String customerEmail;
    private String phone;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private String status;          // PENDING | PROCESSING | SHIPPED | DELIVERED | CANCELLED
    private String paymentStatus;   // UNPAID | PAID | FAILED
    private String paymentMethod;   // COD | VNPAY
    private String note;
    private LocalDateTime createdAt;
    private int itemCount;          // Tổng số loại sách trong đơn
}
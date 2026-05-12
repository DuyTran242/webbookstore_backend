// 1. OrderResponseDTO.java (Dùng cho danh sách đơn hàng ngoài bảng)
package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDTO {
    private Long id;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
}
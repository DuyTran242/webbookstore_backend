// 3. OrderDetailResponseDTO.java (Dùng cho Popup/Modal Chi tiết)
package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResponseDTO {
    private Long id;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private String status;
    private String paymentStatus;
    private String shippingAddress;
    private String phone;
    private String note;
    private List<OrderItemDTO> items;
}
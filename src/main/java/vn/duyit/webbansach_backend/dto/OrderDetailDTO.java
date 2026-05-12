package vn.duyit.webbansach_backend.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderDetailDTO {
    private Long id;
    private String customerName;
    private String phone;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private String status;
    private String paymentStatus;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItemDetailDTO> items;
}

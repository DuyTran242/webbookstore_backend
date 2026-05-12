package vn.duyit.webbansach_backend.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class OrderDTO {
    private Long id;
    private String customerName;
    private String phone;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private String status;
    private String paymentStatus;
    private LocalDateTime createdAt;
}

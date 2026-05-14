package vn.duyit.webbansach_backend.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDetailDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
}

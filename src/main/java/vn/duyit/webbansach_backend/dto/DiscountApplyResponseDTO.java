package vn.duyit.webbansach_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountApplyResponseDTO {
    private boolean success;
    private String message;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
}

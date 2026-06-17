package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DiscountApplyRequestDTO {
    private String code;
    private BigDecimal subtotal;
}

package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCodeDTO {
    private Long id;
    private String code;
    private String description;
    private Integer discountPercentage;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean isActive;
}

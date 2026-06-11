package vn.duyit.webbansach_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class WarrantyInfoDTO {
    private String customerPhone;
    private String productName;
    private String serialNumber;
    private LocalDateTime warrantyEndDate;
    private String warrantyStatus; // "Còn bảo hành" hoặc "Hết hạn"
    private Long orderItemId;
}
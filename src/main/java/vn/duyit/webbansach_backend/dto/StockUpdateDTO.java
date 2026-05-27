package vn.duyit.webbansach_backend.dto;

import lombok.Data;

@Data
public class StockUpdateDTO {
    private Long productId;
    private Integer quantity;
    private String type;
    private String note;
}
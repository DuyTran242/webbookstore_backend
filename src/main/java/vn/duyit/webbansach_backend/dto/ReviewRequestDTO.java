package vn.duyit.webbansach_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDTO {
    private Long productId;
    private Long userId; // Sẽ null nếu là khách vãng lai
    private Integer rating;
    private String comment;
}
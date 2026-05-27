package vn.duyit.webbansach_backend.admin.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewAdminDTO {
    private Long   id;

    // Thông tin sách
    private Long   productId;
    private String productName;
    private String productImage;

    // Thông tin người đánh giá
    private Long   userId;
    private String userName;
    private String userAvatar;
    private String userEmail;

    // Nội dung đánh giá
    private Integer rating;
    private String  comment;
    private LocalDateTime createdAt;
}
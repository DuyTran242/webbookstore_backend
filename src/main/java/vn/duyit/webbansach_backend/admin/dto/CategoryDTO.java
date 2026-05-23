package vn.duyit.webbansach_backend.admin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private LocalDateTime createdAt;
    private Long productCount; // Tổng số sách thuộc danh mục này
}
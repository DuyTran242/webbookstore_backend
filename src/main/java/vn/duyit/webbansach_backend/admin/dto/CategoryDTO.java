package vn.duyit.webbansach_backend.admin.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;      // Tên danh mục cha (để hiển thị)
    private LocalDateTime createdAt;
    private long productCount;      // Số sách thuộc danh mục này
    private List<CategoryDTO> children; // Danh mục con (nếu cần tree view)
}

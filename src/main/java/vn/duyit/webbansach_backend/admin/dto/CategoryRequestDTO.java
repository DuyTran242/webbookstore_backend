package vn.duyit.webbansach_backend.admin.dto;
import lombok.Data;

@Data
public class CategoryRequestDTO {
    private String name;
    private String description;
    private Long parentId;
}
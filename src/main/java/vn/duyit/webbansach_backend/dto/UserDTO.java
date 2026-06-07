package vn.duyit.webbansach_backend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private Integer isDelete;
    private Long roleId; // 2: User, 3: Employee
}

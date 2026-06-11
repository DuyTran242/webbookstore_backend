package vn.duyit.webbansach_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class UserDetailDTO extends UserDTO {
    private String username;
    private String address;
    private LocalDate birthdate;
    private String gender;
    private Integer status;
    private LocalDateTime createdAt;
}
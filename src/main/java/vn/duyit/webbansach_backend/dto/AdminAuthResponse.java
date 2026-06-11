package vn.duyit.webbansach_backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuthResponse {
    private Long id;
    private String email;
    private String role; // "Admin" hoặc "Staff"


}
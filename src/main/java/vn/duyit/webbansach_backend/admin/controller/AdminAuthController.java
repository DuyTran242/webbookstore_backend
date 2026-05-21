package vn.duyit.webbansach_backend.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.dto.AdminAuthResponse;
import vn.duyit.webbansach_backend.admin.dto.AdminLoginRequest;
import vn.duyit.webbansach_backend.admin.service.AdminAuthService;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        try {
            // Gọi tầng Service để kiểm tra logic đăng nhập và phân quyền
            AdminAuthResponse response = adminAuthService.loginAdmin(request);

            // Nếu đăng nhập hợp lệ (Admin hoặc Staff), trả về HTTP status 200 (OK) cùng thông tin
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Nếu có lỗi (sai tài khoản, sai mật khẩu, hoặc user thường cố tình đăng nhập)
            // Trả về HTTP status 400 (Bad Request) kèm theo câu thông báo lỗi từ Service
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

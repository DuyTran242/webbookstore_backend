package vn.duyit.webbansach_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.duyit.webbansach_backend.dto.AdminAuthResponse;
import vn.duyit.webbansach_backend.dto.AdminLoginRequest;
import vn.duyit.webbansach_backend.service.AdminAuthService;

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
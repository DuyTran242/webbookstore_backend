package vn.duyit.webbansach_backend.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            // Trả về lỗi 400 Bad Request kèm message để React xử lý
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Tạo class DTO nhỏ để nhận dữ liệu từ React
    public static class LoginRequest {
        public String email;
        public String password;
    }

    // API Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User loggedInUser = userService.login(loginRequest.email, loginRequest.password);
            return ResponseEntity.ok(loggedInUser); // Trả về 200 OK kèm thông tin user
        } catch (RuntimeException e) {
            // Trả về 400 Bad Request kèm mã lỗi
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam String token) {
        boolean isActivated = userService.activateAccount(token);
        if (isActivated) {
            return ResponseEntity.ok("ACTIVATED_SUCCESS");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TOKEN");
        }
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    // Lớp DTO nhận request OTP
    public static class ResetPasswordRequest {
        public String email;
        public String otp;
        public String newPassword;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            userService.generateAndSendOtp(email);
            return ResponseEntity.ok("OTP_SENT_SUCCESS");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Thêm API Đăng nhập bằng Facebook
    @PostMapping("/facebook-login")
    public ResponseEntity<?> facebookLogin(@RequestBody User fbUser) {
        try {
            User loggedInUser = userService.loginWithFacebook(fbUser);
            return ResponseEntity.ok(loggedInUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi đăng nhập Facebook: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.verifyOtpAndUpdatePassword(request.email, request.otp, request.newPassword);
            return ResponseEntity.ok("PASSWORD_UPDATED_SUCCESS");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Thêm API Đăng nhập bằng Google
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody User googleUser) {
        try {
            User loggedInUser = userService.loginWithGoogle(googleUser);
            return ResponseEntity.ok(loggedInUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi đăng nhập Google: " + e.getMessage());
        }
    }

    // API Lấy thông tin chi tiết của 1 User
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // API Cập nhật thông tin User (Đã sửa lại thêm email)
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("id") Long id,
            @RequestParam("email") String email, // <-- Đã bổ sung biến email
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("gender") String gender,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "birthdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        try {
            // Truyền thêm email và address vào hàm gọi Service
            User updatedUser = userService.updateUserProfile(
                    id, email, fullName, phone, gender, address, birthdate, avatarFile
            );
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) { // <-- Đổi thành Exception để bắt lỗi ghi file avatar
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // ==========================================
    // THÊM MỚI: DTO VÀ API ĐỔI MẬT KHẨU
    // ==========================================
    public static class ChangePasswordRequest {
        public Long id;
        public String currentPassword;
        public String newPassword;
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            // Gọi logic xử lý từ UserService
            userService.changePassword(request.id, request.currentPassword, request.newPassword);
            return ResponseEntity.ok("Cập nhật mật khẩu mới thành công");
        } catch (RuntimeException e) {
            // Bắt lỗi và ném về React để hiển thị thông báo (ví dụ: sai mật khẩu hiện tại)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
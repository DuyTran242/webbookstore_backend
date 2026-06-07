package vn.duyit.webbansach_backend.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.admin.dto.AdminAuthResponse;
import vn.duyit.webbansach_backend.admin.dto.AdminLoginRequest;
import vn.duyit.webbansach_backend.entity.Role;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.repository.UserRepository;

import java.util.Optional;

@Service
public class AdminAuthService {

    @Autowired
    private UserRepository userRepository;

    public AdminAuthResponse loginAdmin(AdminLoginRequest request) {
        // 1. Tìm kiếm User trong database thông qua email
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác!");
        }

        User user = optionalUser.get();

        // 2. Kiểm tra mật khẩu
        // Lưu ý: Ở đây đang so sánh chuỗi thô. Nếu database của bạn lưu mật khẩu đã mã hóa (BCrypt),
        // bạn cần inject PasswordEncoder và dùng passwordEncoder.matches(request.getPassword(), user.getPassword())
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác!");
        }

        // 3. Kiểm tra Role (role_id)
        String userRole = null;

        // Giả sử Entity User của bạn có một danh sách quyền (Set<Role> roles hoặc List<Role> roles)
        for (Role role : user.getRoles()) {
            if (role.getId() == 1) {
                userRole = "Admin";
                break; // Quyền Admin là cao nhất, nếu thấy thì thoát vòng lặp luôn
            } else if (role.getId() == 3) {
                userRole = "Staff"; // Nếu là Staff thì ghi nhận, nhưng vẫn duyệt tiếp lỡ có quyền Admin
            }
        }

        // 4. Nếu userRole vẫn null (tức là chỉ có role_id = 2 hoặc không có quyền hợp lệ)
        if (userRole == null) {
            throw new RuntimeException("Bạn không có quyền truy cập vào hệ thống quản trị!");
        }

        // 5. Nếu hợp lệ, trả về đối tượng Response (DTO)
        return new AdminAuthResponse(user.getId(), user.getEmail(), userRole);
    }
}

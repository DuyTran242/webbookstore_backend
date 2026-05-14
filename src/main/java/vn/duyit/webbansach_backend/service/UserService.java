package vn.duyit.webbansach_backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.entity.UserRole;
import vn.duyit.webbansach_backend.repository.UserRepository;
import vn.duyit.webbansach_backend.repository.UserRoleRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    // Đã gom các repository và mailSender vào constructor chung cho chuẩn Spring Boot
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.mailSender = mailSender;
    }

    public User register(User user) {
        // Kiểm tra trùng lặp
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("EMAIL_EXISTS");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("PHONE_EXISTS");
        }

        // Set các giá trị mặc định
        user.setStatus(0); // 0: Chưa kích hoạt
        user.setCreatedAt(LocalDateTime.now());

        // Tạo mã kích hoạt ngẫu nhiên
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        User savedUser = userRepository.save(user);

        // Lưu role mặc định
        UserRole userRole = new UserRole();
        userRole.setUserId(savedUser.getId());
        userRole.setRoleId(2L);
        userRoleRepository.save(userRole);

        // Gửi email
        String activationLink = "http://localhost:3000/activate?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Kích hoạt tài khoản Wolf Bed");
        message.setText("Chào " + user.getFullName() + ",\n\n" +
                "Chúc mừng bạn đã đăng ký tài khoản thành công. Vui lòng click vào đường link dưới đây để kích hoạt tài khoản của bạn:\n" +
                activationLink + "\n\nTrân trọng,\nĐội ngũ Wolf Bed");
        mailSender.send(message);

        return savedUser;
    }

    // Hàm xử lý kích hoạt
    public boolean activateAccount(String token) {
        User user = userRepository.findByVerificationToken(token).orElse(null);
        if (user != null) {
            user.setStatus(1); // 1: Đã kích hoạt
            user.setVerificationToken(null); // Xóa token sau khi dùng
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Hàm xử lý đăng nhập
    public User login(String email, String password) {
        // Tìm user trong DB theo email
        User user = userRepository.findByEmail(email).orElse(null);

        // 1. Kiểm tra tài khoản có tồn tại và mật khẩu có khớp không
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("INVALID_CREDENTIALS");
        }

        // 2. Kiểm tra trạng thái kích hoạt (status = 1)
        if (user.getStatus() == 0) {
            throw new RuntimeException("NOT_ACTIVATED");
        }

        // Thành công thì trả về thông tin user
        return user;
    }

    public void generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        // Tạo mã OTP ngẫu nhiên 6 số
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        // Lưu OTP và thời gian hết hạn (ví dụ: 5 phút)
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // Gửi email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Mã xác thực lấy lại mật khẩu Wolf Bed");
        message.setText("Chào " + user.getFullName() + ",\n\n" +
                "Mã xác thực (OTP) để lấy lại mật khẩu của bạn là: " + otp + "\n" +
                "Mã này sẽ hết hạn trong vòng 5 phút.\n\n" +
                "Trân trọng,\nĐội ngũ Wolf Bed");
        mailSender.send(message);
    }

    // 2. Hàm xác thực OTP và đổi mật khẩu
    public void verifyOtpAndUpdatePassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        // Kiểm tra mã OTP
        if (user.getOtpCode() == null || !user.getOtpCode().equals(otp)) {
            throw new RuntimeException("INVALID_OTP");
        }

        // Kiểm tra thời gian hết hạn
        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("EXPIRED_OTP");
        }

        // Cập nhật mật khẩu mới và xóa OTP cũ
        user.setPassword(newPassword); // Lưu ý: Nếu có băm mật khẩu (BCrypt) thì nhớ băm ở đây
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
    }

    // Hàm xử lý đăng nhập Facebook
    public User loginWithFacebook(User fbUser) {
        // Kiểm tra xem ID Facebook (được lưu ở cột phone) đã tồn tại chưa
        Optional<User> existingUser = userRepository.findByPhone(fbUser.getPhone());

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            fbUser.setStatus(1); // 1: Kích hoạt ngay
            fbUser.setProvider("FB");
            fbUser.setCreatedAt(LocalDateTime.now());
            fbUser.setUpdatedAt(LocalDateTime.now());
            fbUser.setPassword("FB_" + fbUser.getPhone());

            User savedUser = userRepository.save(fbUser);

            UserRole userRole = new UserRole();
            userRole.setUserId(savedUser.getId());
            userRole.setRoleId(2L);
            userRoleRepository.save(userRole);

            return savedUser;
        }
    }

    // Hàm xử lý đăng nhập Google
    public User loginWithGoogle(User googleUser) {
        Optional<User> existingUser = userRepository.findByEmail(googleUser.getEmail());

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            googleUser.setStatus(1); // 1: Kích hoạt ngay
            googleUser.setProvider("GOOGLE");
            googleUser.setCreatedAt(LocalDateTime.now());
            googleUser.setUpdatedAt(LocalDateTime.now());
            googleUser.setPassword("GOO_" + googleUser.getEmail());

            User savedUser = userRepository.save(googleUser);

            UserRole userRole = new UserRole();
            userRole.setUserId(savedUser.getId());
            userRole.setRoleId(2L);
            userRoleRepository.save(userRole);

            return savedUser;
        }
    }

    // ====== ĐÃ SỬA THAM SỐ TRUYỀN VÀO Ở ĐÂY ======
    public User updateUserProfile(Long id, String email, String fullName, String phone,
                                  String gender, String address, LocalDate birthdate, MultipartFile avatar) throws Exception {

        // 1. Tìm user trong Database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Cập nhật các trường thông tin cơ bản
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setGender(gender);
        user.setAddress(address);
        user.setBirthdate(birthdate); // Mất báo lỗi đỏ do đã khớp kiểu dữ liệu

        // 3. Xử lý lưu ảnh nếu có file gửi lên
        if (avatar != null && !avatar.isEmpty()) {
            Path uploadPath = Paths.get("uploads/avatars/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file ngẫu nhiên để không bị trùng lặp
            String fileName = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Lưu đường dẫn vào Database
            user.setAvatar("/avatars/" + fileName);
        }

        // 4. Lưu lại toàn bộ xuống Database
        return userRepository.save(user);
    }
    // ====== THÊM MỚI: HÀM ĐỔI MẬT KHẨU ======
    public void changePassword(Long id, String currentPassword, String newPassword) {
        // 1. Tìm user trong Database
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Kiểm tra mật khẩu hiện tại (Đang so sánh chuỗi giống với hàm login của bạn)
        if (!user.getPassword().equals(currentPassword)) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng vui lòng kiểm tra lại");
        }

        // 3. Cập nhật mật khẩu mới
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now()); // Cập nhật lại thời gian thay đổi

        // 4. Lưu vào Database
        userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }
}
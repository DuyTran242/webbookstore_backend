package vn.duyit.webbansach_backend.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.admin.dto.AdminUserDTO;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.repository.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<AdminUserDTO> getAllUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepository.findByFullNameContainingOrEmailContaining(
                    keyword.trim(), keyword.trim(), pageable
            );
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(this::mapToDTO);
    }

    public AdminUserDTO getUserDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        return mapToDTO(user);
    }

    public AdminUserDTO lockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        if (user.getStatus() == 2) {
            throw new RuntimeException("Tài khoản này đã bị khóa trước đó!");
        }

        user.setStatus(2);
        return mapToDTO(userRepository.save(user));
    }

    public AdminUserDTO unlockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        if (user.getStatus() != 2) {
            throw new RuntimeException("Tài khoản này không ở trạng thái bị khóa!");
        }

        user.setStatus(1);
        return mapToDTO(userRepository.save(user));
    }

    private AdminUserDTO mapToDTO(User user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setBirthdate(user.getBirthdate());
        dto.setAvatar(user.getAvatar());
        dto.setProvider(user.getProvider());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}

package vn.duyit.webbansach_backend.orderinface;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.UserDTO;
import vn.duyit.webbansach_backend.dto.UserDetailDTO;
import vn.duyit.webbansach_backend.entity.Role;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.orderinface.UserService;
import vn.duyit.webbansach_backend.repository.RoleRepository;
import vn.duyit.webbansach_backend.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<UserDTO> getUsersByRole(Long roleId) {
        // Lấy user chưa bị khóa (isDelete = 0)
        return userRepository.findByRoles_IdAndIsDeleteIsNullOrIsDelete(roleId, 0).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getLockedUsers() {
        return userRepository.findByIsDelete(1).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailDTO getUserDetails(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        UserDetailDTO dto = new UserDetailDTO();
        BeanUtils.copyProperties(user, dto);
        // Map role đầu tiên cho đơn giản
        user.getRoles().stream().findFirst().ifPresent(role -> dto.setRoleId(role.getId()));
        return dto;
    }

    @Override
    public void toggleLockUser(Long id, Integer isDeleteStatus) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsDelete(isDeleteStatus);
        userRepository.save(user);
    }

    @Override
    public void promoteToEmployee(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Role employeeRole = roleRepository.findById(3L).orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().clear();
        user.getRoles().add(employeeRole);
        userRepository.save(user);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        user.getRoles().stream().findFirst().ifPresent(role -> dto.setRoleId(role.getId()));
        return dto;
    }
}

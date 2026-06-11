package vn.duyit.webbansach_backend.orderinface;

import vn.duyit.webbansach_backend.dto.UserDTO;
import vn.duyit.webbansach_backend.dto.UserDetailDTO;

import java.util.List;
public interface UserService {
    List<UserDTO> getUsersByRole(Long roleId);
    List<UserDTO> getLockedUsers();
    UserDetailDTO getUserDetails(Long id);
    void toggleLockUser(Long id, Integer isDeleteStatus);
    void promoteToEmployee(Long id);
}

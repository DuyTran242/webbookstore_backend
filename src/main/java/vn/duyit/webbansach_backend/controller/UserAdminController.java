package vn.duyit.webbansach_backend.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.UserDTO;
import vn.duyit.webbansach_backend.dto.UserDetailDTO;
import vn.duyit.webbansach_backend.orderinface.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(userService.getUsersByRole(roleId));
    }

    @GetMapping("/locked")
    public ResponseEntity<List<UserDTO>> getLockedUsers() {
        return ResponseEntity.ok(userService.getLockedUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDTO> getUserDetails(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDetails(id));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<?> toggleLockUser(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        userService.toggleLockUser(id, payload.get("isDelete"));
        return ResponseEntity.ok().body(Map.of("message", "Cập nhật trạng thái thành công"));
    }

    @PutMapping("/{id}/promote")
    public ResponseEntity<?> promoteToEmployee(@PathVariable Long id) {
        userService.promoteToEmployee(id);
        return ResponseEntity.ok().body(Map.of("message", "Thăng cấp nhân viên thành công"));
    }
}

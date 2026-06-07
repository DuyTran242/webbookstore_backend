package vn.duyit.webbansach_backend.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.dto.AdminUserDTO;
import vn.duyit.webbansach_backend.admin.service.AdminUserService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminUserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    String keyword
    ) {
        Page<AdminUserDTO> result = adminUserService.getAllUsers(page, size, keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetail(@PathVariable Long id) {
        try {
            AdminUserDTO dto = adminUserService.getUserDetail(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<?> lockUser(@PathVariable Long id) {
        try {
            AdminUserDTO dto = adminUserService.lockUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Đã khóa tài khoản thành công",
                    "user", dto
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable Long id) {
        try {
            AdminUserDTO dto = adminUserService.unlockUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Đã mở khóa tài khoản thành công",
                    "user", dto
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

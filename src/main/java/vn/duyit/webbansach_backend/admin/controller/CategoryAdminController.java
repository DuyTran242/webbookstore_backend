package vn.duyit.webbansach_backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.dto.CategoryDTO;
import vn.duyit.webbansach_backend.admin.service.CategoryAdminService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    // Lấy tất cả danh mục (có tổng số sách)
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryAdminService.getAllCategories());
    }

    // Lấy chi tiết 1 danh mục
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryAdminService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thêm danh mục mới
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryDTO dto) {
        try {
            return ResponseEntity.ok(categoryAdminService.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Sửa danh mục
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        try {
            return ResponseEntity.ok(categoryAdminService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoryAdminService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Xóa danh mục thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
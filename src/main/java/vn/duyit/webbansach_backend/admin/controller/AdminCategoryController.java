package vn.duyit.webbansach_backend.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.service.AdminCategoryService;
import vn.duyit.webbansach_backend.admin.dto.CategoryDTO;
import vn.duyit.webbansach_backend.admin.dto.CategoryRequestDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    // =====================================================
    // GET /api/admin/categories
    // Lấy tất cả danh mục (dạng phẳng), hỗ trợ tìm kiếm
    // Query param: ?keyword=xxx
    // =====================================================
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(required = false) String keyword
    ) {
        List<CategoryDTO> categories = adminCategoryService.getAllCategories(keyword);
        return ResponseEntity.ok(categories);
    }

    // =====================================================
    // GET /api/admin/categories/tree
    // Lấy danh mục dạng cây (cha → con)
    // Dùng cho dropdown chọn danh mục cha khi tạo/sửa
    // =====================================================
    @GetMapping("/tree")
    public ResponseEntity<List<CategoryDTO>> getCategoryTree() {
        List<CategoryDTO> tree = adminCategoryService.getCategoryTree();
        return ResponseEntity.ok(tree);
    }

    // =====================================================
    // GET /api/admin/categories/{id}
    // Lấy chi tiết một danh mục
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            CategoryDTO dto = adminCategoryService.getCategoryById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // =====================================================
    // POST /api/admin/categories
    // Tạo danh mục mới
    // Body: { name, description, parentId }
    // =====================================================
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestDTO request) {
        try {
            CategoryDTO created = adminCategoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================
    // PUT /api/admin/categories/{id}
    // Cập nhật danh mục
    // Body: { name, description, parentId }
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDTO request
    ) {
        try {
            CategoryDTO updated = adminCategoryService.updateCategory(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // =====================================================
    // DELETE /api/admin/categories/{id}
    // Xóa danh mục
    // Sẽ báo lỗi nếu còn sách hoặc danh mục con
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            adminCategoryService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("message", "Xóa danh mục thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
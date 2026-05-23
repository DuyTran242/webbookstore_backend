package vn.duyit.webbansach_backend.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ProductCreateDTO;
import vn.duyit.webbansach_backend.dto.ProductDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.service.ProductService;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    // 1. Tạo sản phẩm mới
    @PostMapping("/api/admin/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDTO dto) {
        try {
            Product created = productService.createProductWithImages(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm sản phẩm: " + e.getMessage());
        }
    }

    // 2. Lấy danh sách sản phẩm có phân trang + tìm kiếm (THÊM MỚI)
    @GetMapping("/api/admin/products")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)    String keyword,
            @RequestParam(required = false)    Long categoryId
    ) {
        Page<ProductDTO> result = productService.getProductsPaged(page, size, keyword, categoryId);
        return ResponseEntity.ok(result);
    }

    // 3. Lấy chi tiết sản phẩm để đưa lên Form Sửa
    @GetMapping("/api/products/admin/{id}")
    public ResponseEntity<ProductDTO> getProductForEdit(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 4. Cập nhật sản phẩm
    @PutMapping("/api/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO dto
    ) {
        ProductDTO updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 5. Xóa sản phẩm (THÊM MỚI — đã fix hàm bị comment trước đó)
    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Xóa sản phẩm thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
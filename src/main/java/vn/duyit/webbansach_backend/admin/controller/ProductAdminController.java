package vn.duyit.webbansach_backend.admin.controller;

import vn.duyit.webbansach_backend.dto.ProductCreateDTO;
import vn.duyit.webbansach_backend.dto.ProductDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    // 1. API tạo sản phẩm mới (Dành cho Admin)
    @PostMapping("/api/admin/products")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        try {
            Product createdProduct = productService.createProductWithImages(productCreateDTO);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm sản phẩm: " + e.getMessage());
        }
    }

    // 2. API lấy dữ liệu sản phẩm để đưa lên Form Sửa (Dành cho Admin)
    @GetMapping("/api/products/admin/{id}")
    public ResponseEntity<ProductDTO> getProductForEdit(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 3. API cập nhật sản phẩm (Dành cho Admin)
    @PutMapping("/api/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }
}

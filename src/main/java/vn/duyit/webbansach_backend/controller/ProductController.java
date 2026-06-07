package vn.duyit.webbansach_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ProductDTO;
import vn.duyit.webbansach_backend.dto.ProductDetailDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000") // Cấp phép cho Frontend gọi API
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 1. API lấy danh sách tất cả sản phẩm
    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    // 2. API lấy chi tiết sản phẩm cho trang giao diện người dùng (Frontend)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProduct(@PathVariable Long id) {
        ProductDetailDTO productDetail = productService.getProductDetail(id);
        if (productDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productDetail);
    }

    // 3. FIX LỖI TRÙNG ĐƯỜNG DẪN: API lấy dữ liệu sản phẩm để đưa lên Form Sửa (Admin)
    // Đổi đường dẫn thành "/admin/{id}" để không trùng với hàm getProduct ở trên
    @GetMapping("/admin/{id}")
    public ResponseEntity<ProductDTO> getProductForEdit(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // 4. API lấy sản phẩm theo danh mục
    @GetMapping("/category/{categoryId}")
    public List<Product> getByCategory(@PathVariable Long categoryId) {
        return productService.getByCategory(categoryId);
    }

    // 5. API cập nhật sản phẩm (Dành cho Admin)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    // 6. API tìm kiếm sản phẩm theo từ khóa
    @GetMapping("/search")
    public List<Product> search(@RequestParam String keyword) {
        return productService.searchProduct(keyword);
    }

    // 7. API tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }
}
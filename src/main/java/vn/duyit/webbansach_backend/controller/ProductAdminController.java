package vn.duyit.webbansach_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ProductCreateDTO;
import vn.duyit.webbansach_backend.dto.ProductDTO2;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.orderinface.ProductService2;
import vn.duyit.webbansach_backend.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    @Autowired
    private ProductService2 productService;
    @Autowired
    private ProductService  productService2;


    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        try {
            Product createdProduct = productService2.createProductWithImages(productCreateDTO);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm sản phẩm: " + e.getMessage());
        }
    }
    // ĐỔI TÊN ĐƯỜNG DẪN Ở ĐÂY ĐỂ TRÁNH XUNG ĐỘT
    @GetMapping("/require-serial")
    public ResponseEntity<?> getProductsWithSerial() {
        try {
            List<ProductDTO2> products = productService.getProductsWithSerial();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
        }
    }
}
package vn.duyit.webbansach_backend.admin.controller;

import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.service.ProductService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final ProductService productService;

    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable Long id){
        // productService.deleteProduct(id);
    }
}

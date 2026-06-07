package vn.duyit.webbansach_backend.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ProductInBrandDTO;
import vn.duyit.webbansach_backend.orderinface.BrandAdminService;

@RestController
@RequestMapping("/api/admin/brands")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BrandAdminController {

    private final BrandAdminService brandAdminService;

    // Lấy sản phẩm theo brand với phân trang
    @GetMapping("/{brandName}/products")
    public ResponseEntity<Page<ProductInBrandDTO>> getProductsByBrand(
            @PathVariable String brandName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ProductInBrandDTO> products = brandAdminService.getProductsByBrand(brandName, PageRequest.of(page, size));
        return ResponseEntity.ok(products);
    }
}
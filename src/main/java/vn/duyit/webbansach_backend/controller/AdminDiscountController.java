package vn.duyit.webbansach_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.DiscountCodeDTO;
import vn.duyit.webbansach_backend.entity.DiscountCode;
import vn.duyit.webbansach_backend.service.DiscountCodeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
public class AdminDiscountController {

    @Autowired
    private DiscountCodeService discountCodeService;

    @GetMapping
    public ResponseEntity<List<DiscountCode>> getAllDiscountCodes() {
        return ResponseEntity.ok(discountCodeService.getAllDiscountCodes());
    }

    @PostMapping
    public ResponseEntity<?> createDiscountCode(@RequestBody DiscountCodeDTO dto) {
        try {
            return ResponseEntity.ok(discountCodeService.createDiscountCode(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscountCode(@PathVariable Long id, @RequestBody DiscountCodeDTO dto) {
        try {
            return ResponseEntity.ok(discountCodeService.updateDiscountCode(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscountCode(@PathVariable Long id) {
        try {
            discountCodeService.deleteDiscountCode(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

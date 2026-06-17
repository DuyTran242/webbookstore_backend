package vn.duyit.webbansach_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.DiscountApplyRequestDTO;
import vn.duyit.webbansach_backend.dto.DiscountApplyResponseDTO;
import vn.duyit.webbansach_backend.service.DiscountCodeService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountCodeService discountCodeService;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveDiscounts() {
        return ResponseEntity.ok(discountCodeService.getActiveDiscountCodes());
    }

    @PostMapping("/apply")
    public ResponseEntity<DiscountApplyResponseDTO> applyDiscount(@RequestBody DiscountApplyRequestDTO request) {
        try {
            BigDecimal discountAmount = discountCodeService.calculateDiscount(request.getCode(), request.getSubtotal());
            BigDecimal finalTotal = request.getSubtotal().subtract(discountAmount);

            if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
                finalTotal = BigDecimal.ZERO;
            }

            DiscountApplyResponseDTO response = new DiscountApplyResponseDTO(true, "Áp dụng mã giảm giá thành công!", discountAmount, finalTotal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DiscountApplyResponseDTO response = new DiscountApplyResponseDTO(false, e.getMessage(), BigDecimal.ZERO, request.getSubtotal());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

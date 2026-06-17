package vn.duyit.webbansach_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.ReviewRequestDTO;
import vn.duyit.webbansach_backend.dto.ReviewResponseDTO;
import vn.duyit.webbansach_backend.service.ProductReviewService;

@RestController
@RequestMapping("/api/productsreview")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(@RequestBody ReviewRequestDTO dto) {
        try {
            ReviewResponseDTO newReview = reviewService.addReview(dto);
            return ResponseEntity.ok(newReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editReview(@PathVariable Long id, @RequestBody ReviewRequestDTO dto) {
        try {
            ReviewResponseDTO updated = reviewService.editReview(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @RequestParam Long userId) {
        try {
            reviewService.deleteReview(id, userId);
            return ResponseEntity.ok("Xoá bình luận thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<java.util.List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/check-purchase")
    public ResponseEntity<Boolean> checkPurchase(@RequestParam Long userId, @RequestParam Long productId) {
        return ResponseEntity.ok(reviewService.checkPurchase(userId, productId));
    }
}
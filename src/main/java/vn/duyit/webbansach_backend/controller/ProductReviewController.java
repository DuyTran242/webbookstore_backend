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
        ReviewResponseDTO newReview = reviewService.addReview(dto);
        return ResponseEntity.ok(newReview);
    }
}
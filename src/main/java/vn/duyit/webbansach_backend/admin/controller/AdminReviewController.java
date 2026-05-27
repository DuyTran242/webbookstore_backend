package vn.duyit.webbansach_backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.service.AdminReviewService;
import vn.duyit.webbansach_backend.admin.dto.ReviewAdminDTO;
import vn.duyit.webbansach_backend.admin.dto.ReviewSummaryDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    // Danh sách, phân trang + lọc

    @GetMapping
    public ResponseEntity<Page<ReviewAdminDTO>> getReviews(
            @RequestParam(defaultValue = "0")       int     page,
            @RequestParam(defaultValue = "15")      int     size,
            @RequestParam(required = false)         String  keyword,
            @RequestParam(required = false)         Integer rating,
            @RequestParam(required = false)         Long    productId,
            @RequestParam(defaultValue = "newest")  String  sortBy
    ) {
        return ResponseEntity.ok(
                adminReviewService.getReviews(page, size, keyword, rating, productId, sortBy));
    }

    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryDTO> getSummary() {
        return ResponseEntity.ok(adminReviewService.getSummary());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            adminReviewService.deleteReview(id);
            return ResponseEntity.ok(Map.of("message", "Xóa đánh giá thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<?> bulkDelete(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");
            if (ids == null || ids.isEmpty())
                return ResponseEntity.badRequest().body("Danh sách ID không được rỗng!");
            int count = adminReviewService.bulkDeleteReviews(ids);
            return ResponseEntity.ok(Map.of(
                    "message",       "Đã xóa " + count + " đánh giá",
                    "deletedCount",  count));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

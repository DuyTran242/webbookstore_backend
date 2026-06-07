package vn.duyit.webbansach_backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.admin.dto.ReviewAdminDTO;
import vn.duyit.webbansach_backend.admin.dto.ReviewSummaryDTO;
import vn.duyit.webbansach_backend.entity.ProductImage;
import vn.duyit.webbansach_backend.entity.ProductReview;
import vn.duyit.webbansach_backend.admin.repository.ReviewAdminRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewAdminRepository reviewAdminRepository;

    // 1. DANH SÁCH ĐÁNH GIÁ

    public Page<ReviewAdminDTO> getReviews(
            int page, int size,
            String keyword, Integer rating, Long productId,
            String sortBy) {

        Sort sort = switch (sortBy != null ? sortBy : "newest") {
            case "oldest"     -> Sort.by("createdAt").ascending();
            case "rating_asc" -> Sort.by("rating").ascending();
            case "rating_desc"-> Sort.by("rating").descending();
            default           -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sort);

        String kw = (keyword != null && !keyword.trim().isEmpty())
                ? keyword.trim() : null;

        Page<ProductReview> reviewPage = reviewAdminRepository
                .searchReviews(kw, rating, productId, pageable);

        return reviewPage.map(this::mapToDTO);
    }

    // 2. THỐNG KÊ TỔNG QUAN

    public ReviewSummaryDTO getSummary() {
        long  total  = reviewAdminRepository.count();
        double avg   = reviewAdminRepository.avgRating();

        Map<Integer, Long> dist = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) dist.put(i, 0L);

        List<Object[]> rows = reviewAdminRepository.countByRating();
        for (Object[] row : rows) {
            dist.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }

        double avgRounded = Math.round(avg * 10.0) / 10.0;

        return new ReviewSummaryDTO(total, avgRounded, dist);
    }


    // 3. XÓA ĐÁNH GIÁ

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewAdminRepository.existsById(reviewId)) {
            throw new RuntimeException("Không tìm thấy đánh giá với ID: " + reviewId);
        }
        reviewAdminRepository.deleteById(reviewId);
    }

    // 4. XÓA HÀNG LOẠT
    @Transactional
    public int bulkDeleteReviews(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            if (reviewAdminRepository.existsById(id)) {
                reviewAdminRepository.deleteById(id);
                count++;
            }
        }
        return count;
    }

    // HELPER: map entity → DTO

    private ReviewAdminDTO mapToDTO(ProductReview r) {
        ReviewAdminDTO dto = new ReviewAdminDTO();
        dto.setId(r.getId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());

        // Thông tin sách
        if (r.getProduct() != null) {
            dto.setProductId(r.getProduct().getId());
            dto.setProductName(r.getProduct().getName());
            // Lấy ảnh bìa chính
            if (r.getProduct().getImages() != null) {
                r.getProduct().getImages().stream()
                        .filter(img -> img.getIsPrimary() != null && img.getIsPrimary() == 1)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .ifPresent(dto::setProductImage);
            }
        }

        // Thông tin người dùng
        if (r.getUser() != null) {
            dto.setUserId(r.getUser().getId());
            dto.setUserEmail(r.getUser().getEmail());
            String name = r.getUser().getFullName();
            dto.setUserName(name != null && !name.isBlank()
                    ? name : r.getUser().getUsername());
            dto.setUserAvatar(r.getUser().getAvatar());
        } else {
            dto.setUserName("Khách vãng lai");
            dto.setUserAvatar(null);
        }

        return dto;
    }
}
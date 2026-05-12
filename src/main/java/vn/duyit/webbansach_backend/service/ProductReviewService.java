package vn.duyit.webbansach_backend.service;

import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.ReviewRequestDTO;
import vn.duyit.webbansach_backend.dto.ReviewResponseDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.entity.ProductReview;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.repository.ProductReviewRepository;
import vn.duyit.webbansach_backend.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class ProductReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductReviewService(ProductReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponseDTO addReview(ReviewRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        // Kiểm tra xem là User đăng nhập hay Khách
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            review.setUser(user);
        }

        ProductReview savedReview = reviewRepository.save(review);
        return mapToResponseDTO(savedReview);
    }

    // Hàm chuyển Entity thành DTO
    public ReviewResponseDTO mapToResponseDTO(ProductReview review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getFullName() != null ? review.getUser().getFullName() : review.getUser().getUsername());
            dto.setUserAvatar(review.getUser().getAvatar());
        } else {
            dto.setUserName("Khách vãng lai");
            dto.setUserAvatar("https://cdn-icons-png.flaticon.com/512/149/149071.png"); // Ảnh avatar mặc định
        }
        return dto;
    }
}
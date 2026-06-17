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
    private final vn.duyit.webbansach_backend.repository.OrderRepository orderRepository;

    public ProductReviewService(ProductReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository, vn.duyit.webbansach_backend.repository.OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public ReviewResponseDTO addReview(ReviewRequestDTO dto) {
        if (dto.getUserId() == null) {
            throw new RuntimeException("Bạn phải đăng nhập để đánh giá");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        boolean isAdmin = user.getRoles() != null && user.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));

        Product product = null;
        if (dto.getParentId() == null) {
            product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            boolean hasBought = orderRepository.existsByUserIdAndOrderItems_ProductIdAndStatus(dto.getUserId(), dto.getProductId(), "Hoàn thành");
            if (!hasBought) {
                hasBought = orderRepository.existsByUserIdAndOrderItems_ProductId(dto.getUserId(), dto.getProductId());
                if (!hasBought && !isAdmin) { 
                     throw new RuntimeException("Bạn phải mua sản phẩm này mới được đánh giá");
                }
            }
        }

        ProductReview review = new ProductReview();
        review.setRating(dto.getRating() != null ? dto.getRating() : 0);
        review.setComment(dto.getComment());
        review.setCreatedAt(java.time.LocalDateTime.now());
        review.setUser(user);

        if (dto.getParentId() != null) {
            ProductReview parent = reviewRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận gốc"));
            review.setParentReview(parent);
            review.setRating(0); // Reply không có rating
            review.setProduct(parent.getProduct());
        } else {
            review.setProduct(product);
        }

        ProductReview savedReview = reviewRepository.save(review);
        return mapToResponseDTO(savedReview);
    }

    public ReviewResponseDTO editReview(Long id, ReviewRequestDTO dto) {
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        if (!review.getUser().getId().equals(dto.getUserId())) {
            throw new RuntimeException("Bạn không có quyền sửa bình luận này");
        }

        if (review.getParentReview() == null && dto.getRating() != null) {
            review.setRating(dto.getRating());
        }
        review.setComment(dto.getComment());
        
        ProductReview updated = reviewRepository.save(review);
        return mapToResponseDTO(updated);
    }

    public void deleteReview(Long id, Long userId) {
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        boolean isAdmin = user.getRoles() != null && user.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
        if (!review.getUser().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xoá bình luận này");
        }

        reviewRepository.delete(review);
    }

    public java.util.List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponseDTO)
                .collect(java.util.stream.Collectors.toList());
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
            dto.setUserId(review.getUser().getId());
        } else {
            dto.setUserName("Khách vãng lai");
            dto.setUserAvatar("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            dto.setUserId(null);
        }

        if (review.getReplies() != null && !review.getReplies().isEmpty()) {
            dto.setReplies(review.getReplies().stream().map(this::mapToResponseDTO).collect(java.util.stream.Collectors.toList()));
        }

        return dto;
    }

    public boolean checkPurchase(Long userId, Long productId) {
        if (userId == null) return false;
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        
        boolean isAdmin = user.getRoles() != null && user.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));
        if (isAdmin) return true;

        boolean hasBought = orderRepository.existsByUserIdAndOrderItems_ProductIdAndStatus(userId, productId, "Hoàn thành");
        if (!hasBought) {
            hasBought = orderRepository.existsByUserIdAndOrderItems_ProductId(userId, productId);
        }
        return hasBought;
    }
}
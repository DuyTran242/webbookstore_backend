package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.ProductReview;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductId(Long productId);

    List<ProductReview> findByProductIdAndParentReviewIsNullOrderByCreatedAtDesc(Long productId);

    List<ProductReview> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId AND r.parentReview IS NULL")
    Double getAverageRatingByProductId(@org.springframework.data.repository.query.Param("productId") Long productId);

}
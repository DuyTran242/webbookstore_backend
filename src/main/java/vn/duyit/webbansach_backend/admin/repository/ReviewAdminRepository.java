package vn.duyit.webbansach_backend.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.duyit.webbansach_backend.entity.ProductReview;

public interface ReviewAdminRepository extends JpaRepository<ProductReview, Long> {

    // Tìm kiếm + lọc đánh giá
    @Query("SELECT r FROM ProductReview r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN r.product p " +
            "WHERE (:keyword  IS NULL OR " +
            "       LOWER(p.name)     LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "       LOWER(r.comment)  LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "       (u IS NOT NULL AND LOWER(u.fullName) LIKE LOWER(CONCAT('%',:keyword,'%')))) " +
            "AND (:rating IS NULL OR r.rating = :rating) " +
            "AND (:productId IS NULL OR p.id = :productId)")
    Page<ProductReview> searchReviews(
            @Param("keyword")   String  keyword,
            @Param("rating")    Integer rating,
            @Param("productId") Long    productId,
            Pageable pageable
    );

    // Thống kê số đánh giá theo từng số sao
    @Query("SELECT r.rating, COUNT(r) FROM ProductReview r GROUP BY r.rating ORDER BY r.rating")
    java.util.List<Object[]> countByRating();

    // Tổng số đánh giá
    long count();

    // Trung bình sao toàn hệ thống
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM ProductReview r")
    double avgRating();
}

package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.ProductView;

import java.util.Optional;

public interface ProductViewRepository extends JpaRepository<ProductView, Long> {

    Optional<ProductView> findByProductId(Long productId);

}
package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.ProductSerial;

import java.util.Optional;

public interface ProductSerialRepository extends JpaRepository<ProductSerial, Long> {
    Optional<ProductSerial> findBySerialNumber(String serialNumber);
    int countByProductIdAndStatus(Long productId, String status);
}
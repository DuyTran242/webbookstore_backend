package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.Payment;

import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Tìm Payment dựa vào ID của đối tượng Order bên trong nó
    Optional<Payment> findByOrder_Id(Long orderId);

}
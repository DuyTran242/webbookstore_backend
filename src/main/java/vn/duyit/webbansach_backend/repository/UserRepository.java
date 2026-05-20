package vn.duyit.webbansach_backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByVerificationToken(String verificationToken);
    // BỔ SUNG HÀM NÀY: Dùng để tìm user qua số điện thoại
    Optional<User> findByPhone(String phone);

      // THÊM MỚI: Tìm kiếm theo tên hoặc email, có phân trang
    Page<User> findByFullNameContainingOrEmailContaining(
            String fullName, String email, Pageable pageable
    );
}
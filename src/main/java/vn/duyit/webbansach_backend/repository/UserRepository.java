package vn.duyit.webbansach_backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.duyit.webbansach_backend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByVerificationToken(String verificationToken);
    // BỔ SUNG HÀM NÀY: Dùng để tìm user qua số điện thoại
    Optional<User> findByPhone(String phone);
    // Tìm người dùng theo Role và trạng thái chưa bị xóa (isDelete = 0 hoặc null)
    List<User> findByRoles_IdAndIsDeleteIsNullOrIsDelete(Long roleId, Integer isDelete);

    // Tìm tất cả tài khoản bị khóa (isDelete = 1)
    List<User> findByIsDelete(Integer isDelete);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM User u WHERE u.isDelete IS NULL OR u.isDelete = 0")
    long countActiveUsers();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM User u WHERE (u.isDelete IS NULL OR u.isDelete = 0) AND MONTH(u.createdAt) = MONTH(CURRENT_DATE) AND YEAR(u.createdAt) = YEAR(CURRENT_DATE)")
    long countNewUsersThisMonth();


      // THÊM MỚI: Tìm kiếm theo tên hoặc email, có phân trang
    Page<User> findByFullNameContainingOrEmailContaining(
            String fullName, String email, Pageable pageable
    );
}
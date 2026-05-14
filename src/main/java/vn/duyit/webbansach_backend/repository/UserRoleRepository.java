package vn.duyit.webbansach_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.UserRole;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(Long userId);

}
package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.duyit.webbansach_backend.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Lấy danh mục gốc (không có cha)
    List<Category> findByParentIdIsNull();

    // Lấy danh mục con theo cha
    List<Category> findByParentId(Long parentId);

    // Kiểm tra tên đã tồn tại chưa (để tránh trùng)
    boolean existsByName(String name);

    // Kiểm tra tên tồn tại nhưng không phải chính nó (dùng khi update)
    boolean existsByNameAndIdNot(String name, Long id);

    // Tìm kiếm theo tên
    List<Category> findByNameContainingIgnoreCase(String keyword);

    // Đếm số sản phẩm trong danh mục (để kiểm tra trước khi xóa)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsByCategoryId(Long categoryId);
}
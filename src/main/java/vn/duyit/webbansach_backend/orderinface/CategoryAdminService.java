package vn.duyit.webbansach_backend.orderinface;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.duyit.webbansach_backend.dto.CategoryDTO;
import vn.duyit.webbansach_backend.dto.ProductInCategoryDTO;

import java.util.List;

public interface CategoryAdminService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    Page<ProductInCategoryDTO> getProductsByCategory(Long categoryId, Pageable pageable);
}

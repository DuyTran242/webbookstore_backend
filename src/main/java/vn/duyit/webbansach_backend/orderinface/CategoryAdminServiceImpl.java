package vn.duyit.webbansach_backend.orderinface;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.CategoryDTO;
import vn.duyit.webbansach_backend.dto.ProductInCategoryDTO;
import vn.duyit.webbansach_backend.entity.Category;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.orderinface.CategoryAdminService;
import vn.duyit.webbansach_backend.repository.CategoryRepository;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.service.ProductImageService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(cat -> {
            CategoryDTO dto = new CategoryDTO();
            BeanUtils.copyProperties(cat, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        Category savedCategory = categoryRepository.save(category);

        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(savedCategory, dto);
        return dto;
    }

    @Override
    public void deleteCategory(Long id) {
        // Kiểm tra xem danh mục có sản phẩm nào không
        List<Product> products = productRepository.findByCategoryId(id);
        if (!products.isEmpty()) {
            throw new RuntimeException("Không thể xóa danh mục này vì vẫn còn chứa sản phẩm!");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Page<ProductInCategoryDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        return productPage.map(product -> {
            ProductInCategoryDTO dto = new ProductInCategoryDTO();
            BeanUtils.copyProperties(product, dto);
            // Lấy ảnh chính từ service bạn đã viết
            dto.setPrimaryImage(productImageService.getPrimaryImageUrlByProductId(product.getId()));
            return dto;
        });
    }
}

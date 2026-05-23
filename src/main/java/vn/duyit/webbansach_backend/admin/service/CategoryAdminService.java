package vn.duyit.webbansach_backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.admin.dto.CategoryDTO;
import vn.duyit.webbansach_backend.entity.Category;
import vn.duyit.webbansach_backend.repository.CategoryRepository;
import vn.duyit.webbansach_backend.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getById(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục ID: " + id));
        return mapToDTO(cat);
    }

    public CategoryDTO create(CategoryDTO dto) {
        // Kiểm tra tên bị trùng
        boolean exists = categoryRepository.findAll()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(dto.getName().trim()));
        if (exists) {
            throw new RuntimeException("Tên danh mục \"" + dto.getName() + "\" đã tồn tại!");
        }

        Category cat = new Category();
        cat.setName(dto.getName().trim());
        cat.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        cat.setParentId(dto.getParentId());
        cat.setCreatedAt(LocalDateTime.now());

        return mapToDTO(categoryRepository.save(cat));
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục ID: " + id));

        // Kiểm tra tên trùng với danh mục KHÁC
        boolean duplicateName = categoryRepository.findAll()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(dto.getName().trim())
                        && !c.getId().equals(id));
        if (duplicateName) {
            throw new RuntimeException("Tên danh mục \"" + dto.getName() + "\" đã tồn tại!");
        }

        cat.setName(dto.getName().trim());
        cat.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        cat.setParentId(dto.getParentId());

        return mapToDTO(categoryRepository.save(cat));
    }

    public void delete(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục ID: " + id));

        // Kiểm tra danh mục còn sách không
        long count = productRepository.findByCategoryId(id).size();
        if (count > 0) {
            throw new RuntimeException(
                    "Không thể xóa! Danh mục này đang có " + count + " cuốn sách. " +
                            "Vui lòng chuyển sách sang danh mục khác trước."
            );
        }

        categoryRepository.deleteById(id);
    }

    private CategoryDTO mapToDTO(Category cat) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setDescription(cat.getDescription());
        dto.setParentId(cat.getParentId());
        dto.setCreatedAt(cat.getCreatedAt());
        // Đếm số sách thuộc danh mục này
        dto.setProductCount((long) productRepository.findByCategoryId(cat.getId()).size());
        return dto;
    }
}
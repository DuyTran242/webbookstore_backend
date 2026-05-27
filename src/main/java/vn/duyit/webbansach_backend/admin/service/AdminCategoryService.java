package vn.duyit.webbansach_backend.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.admin.dto.CategoryDTO;
import vn.duyit.webbansach_backend.admin.dto.CategoryRequestDTO;
import vn.duyit.webbansach_backend.entity.Category;
import vn.duyit.webbansach_backend.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public AdminCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // =====================================================
    // 1. LẤY TẤT CẢ DANH MỤC (dạng phẳng, kèm số sách)
    // =====================================================
    public List<CategoryDTO> getAllCategories(String keyword) {
        List<Category> categories;

        if (keyword != null && !keyword.trim().isEmpty()) {
            categories = categoryRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else {
            categories = categoryRepository.findAll();
        }

        return categories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 2. LẤY DANH MỤC DẠNG CÂY (tree: cha → con)
    // =====================================================
    public List<CategoryDTO> getCategoryTree() {
        // Lấy tất cả danh mục gốc (parentId = null)
        List<Category> rootCategories = categoryRepository.findByParentIdIsNull();

        return rootCategories.stream()
                .map(this::mapToDTOWithChildren)
                .collect(Collectors.toList());
    }

    // =====================================================
    // 3. LẤY CHI TIẾT MỘT DANH MỤC
    // =====================================================
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
        return mapToDTOWithChildren(category);
    }

    // =====================================================
    // 4. TẠO DANH MỤC MỚI
    // =====================================================
    @Transactional
    public CategoryDTO createCategory(CategoryRequestDTO request) {
        // Validation: tên không được rỗng
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống!");
        }

        // Kiểm tra trùng tên
        if (categoryRepository.existsByName(request.getName().trim())) {
            throw new RuntimeException("Tên danh mục \"" + request.getName() + "\" đã tồn tại!");
        }

        // Kiểm tra danh mục cha có tồn tại không
        if (request.getParentId() != null) {
            boolean parentExists = categoryRepository.existsById(request.getParentId());
            if (!parentExists) {
                throw new RuntimeException("Danh mục cha với ID " + request.getParentId() + " không tồn tại!");
            }
        }

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setCreatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    // =====================================================
    // 5. CẬP NHẬT DANH MỤC
    // =====================================================
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Validation tên
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống!");
        }

        // Kiểm tra trùng tên (trừ chính nó)
        if (categoryRepository.existsByNameAndIdNot(request.getName().trim(), id)) {
            throw new RuntimeException("Tên danh mục \"" + request.getName() + "\" đã tồn tại!");
        }

        // Không cho phép đặt chính nó làm cha của chính nó
        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new RuntimeException("Danh mục không thể là cha của chính nó!");
        }

        // Không cho phép đặt danh mục con làm cha
        if (request.getParentId() != null) {
            boolean parentExists = categoryRepository.existsById(request.getParentId());
            if (!parentExists) {
                throw new RuntimeException("Danh mục cha với ID " + request.getParentId() + " không tồn tại!");
            }
            // Kiểm tra vòng lặp: parentId không được là con của category hiện tại
            if (isDescendant(id, request.getParentId())) {
                throw new RuntimeException("Không thể đặt danh mục con làm cha của danh mục hiện tại!");
            }
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());

        return mapToDTO(categoryRepository.save(category));
    }

    // =====================================================
    // 6. XÓA DANH MỤC
    // =====================================================
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));

        // Kiểm tra có sách nào đang dùng danh mục này không
        long productCount = categoryRepository.countProductsByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException(
                    "Không thể xóa! Danh mục \"" + category.getName() + "\" đang có " + productCount + " sách. " +
                            "Vui lòng chuyển sách sang danh mục khác trước khi xóa."
            );
        }

        // Kiểm tra có danh mục con không
        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException(
                    "Không thể xóa! Danh mục \"" + category.getName() + "\" đang có " + children.size() + " danh mục con. " +
                            "Vui lòng xóa hoặc chuyển danh mục con trước."
            );
        }

        categoryRepository.deleteById(id);
    }

    // =====================================================
    // HELPER: Kiểm tra targetId có phải là con cháu của ancestorId không
    // =====================================================
    private boolean isDescendant(Long ancestorId, Long targetId) {
        List<Category> children = categoryRepository.findByParentId(ancestorId);
        for (Category child : children) {
            if (child.getId().equals(targetId)) return true;
            if (isDescendant(child.getId(), targetId)) return true;
        }
        return false;
    }

    // =====================================================
    // HELPER: Map Entity → DTO (không kèm children)
    // =====================================================
    private CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParentId());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setProductCount(categoryRepository.countProductsByCategoryId(category.getId()));

        // Lấy tên danh mục cha
        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId())
                    .ifPresent(parent -> dto.setParentName(parent.getName()));
        }

        return dto;
    }

    // =====================================================
    // HELPER: Map Entity → DTO (kèm children để hiển thị tree)
    // =====================================================
    private CategoryDTO mapToDTOWithChildren(Category category) {
        CategoryDTO dto = mapToDTO(category);

        List<Category> children = categoryRepository.findByParentId(category.getId());
        if (!children.isEmpty()) {
            dto.setChildren(
                    children.stream()
                            .map(this::mapToDTOWithChildren) // Đệ quy để lấy cả cháu
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
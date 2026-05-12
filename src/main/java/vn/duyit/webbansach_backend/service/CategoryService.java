package vn.duyit.webbansach_backend.service;

import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.entity.Category;
import vn.duyit.webbansach_backend.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lấy toàn bộ danh mục để Front-end làm Menu và Bộ lọc
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
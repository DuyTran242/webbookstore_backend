package vn.duyit.webbansach_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.dto.ProductCreateDTO;
import vn.duyit.webbansach_backend.dto.ProductDetailDTO;
import vn.duyit.webbansach_backend.dto.ProductImageDTO;
import vn.duyit.webbansach_backend.dto.ReviewResponseDTO;
import vn.duyit.webbansach_backend.entity.Category;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.entity.ProductImage;
import vn.duyit.webbansach_backend.repository.ProductImageRepository;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Thêm import này cho hàm map List

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReviewService reviewService;
    private final ProductImageRepository productImageRepository;

    // Sử dụng Constructor Injection chuẩn của Spring Boot
    public ProductService(ProductRepository productRepository, ProductReviewService reviewService, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.reviewService = reviewService;
        this.productImageRepository = productImageRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Hàm này giữ lại nếu các chức năng khác (như Admin) vẫn cần gọi Entity gốc
    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProduct(String keyword) {
        return productRepository.findByNameContaining(keyword);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Hàm này dùng riêng cho trang Chi tiết sản phẩm phía Frontend
    public ProductDetailDTO getProductDetail(Long id) {
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) return null;

        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStockQuantity(p.getStockQuantity());

        if (p.getCategory() != null) {
            dto.setCategoryName(p.getCategory().getName());
            dto.setCategoryId(p.getCategory().getId());
        }

        dto.setBrand(p.getBrand());
        dto.setMaterial(p.getMaterial());
        dto.setColor(p.getColor());
        dto.setWeight(p.getWeight());
        dto.setImages(p.getImages());

        // Convert List<ProductReview> sang List<ReviewResponseDTO>
        if (p.getReviews() != null) {
            List<ReviewResponseDTO> reviewDTOs = p.getReviews().stream()
                    .map(reviewService::mapToResponseDTO)
                    .toList();
            dto.setReviews(reviewDTOs);
        }

        return dto;
    }

    @Transactional
    public Product createProductWithImages(ProductCreateDTO dto) {
        // 1. Map DTO sang Entity Product
        Product product = new Product();
        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setColor(dto.getColor());
        product.setDescription(dto.getDescription());
        product.setMaterial(dto.getMaterial());

        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getWeight() != null) {
            product.setWeight(dto.getWeight());
        }

        // Gán Category dựa vào categoryId từ DTO
        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            product.setCategory(category);
        }

        product.setStockQuantity(dto.getStockQuantity());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : 1); // Mặc định là 1 (Active)

        // Cập nhật thời gian
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Lưu Product vào DB để lấy ID
        Product savedProduct = productRepository.save(product);

        // 2. Xử lý lưu hình ảnh vào bảng product_images
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<ProductImage> imageEntities = new ArrayList<>();
            for (ProductImageDTO imgDto : dto.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(imgDto.getImageUrl());
                image.setIsPrimary(imgDto.getIsPrimary());
                image.setProduct(savedProduct); // Set khóa ngoại
                imageEntities.add(image);
            }
            productImageRepository.saveAll(imageEntities);
        }

        return savedProduct;
    }

    // 1. Hàm lấy chi tiết sản phẩm để hiển thị lên Form
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setBrand(product.getBrand());
        dto.setColor(product.getColor());
        dto.setDescription(product.getDescription());
        dto.setMaterial(product.getMaterial());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setWeight(product.getWeight());

        // FIX: Lấy CategoryId thông qua object Category
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }

        // Map images entity sang DTO
        if (product.getImages() != null) {
            List<ProductImageDTO> imageDTOs = product.getImages().stream().map(img -> {
                ProductImageDTO imgDto = new ProductImageDTO();
                imgDto.setImageUrl(img.getImageUrl()); // Dữ liệu Base64
                imgDto.setIsPrimary(img.getIsPrimary());
                return imgDto;
            }).collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }
        return dto;
    }

    // 2. Hàm lưu dữ liệu Update
    @Transactional // Rất quan trọng để rollback nếu lỗi xóa/lưu ảnh
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Cập nhật thông tin cơ bản
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setBrand(dto.getBrand());
        product.setColor(dto.getColor());
        product.setDescription(dto.getDescription());
        product.setMaterial(dto.getMaterial());
        product.setStockQuantity(dto.getStockQuantity());
        product.setWeight(dto.getWeight());
        product.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian sửa

        // FIX: Xử lý set lại CategoryId bằng cách tạo object Category
        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            product.setCategory(category);
        } else {
            product.setCategory(null); // Nếu Frontend gửi lên null thì xóa Category
        }

        Product savedProduct = productRepository.save(product);

        // Xử lý hình ảnh: Xóa toàn bộ ảnh cũ trong DB
        productImageRepository.deleteByProductId(savedProduct.getId());

        // Lưu danh sách ảnh mới (hoặc ảnh giữ lại) từ Frontend gửi lên
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<ProductImage> newImages = new ArrayList<>();
            for (ProductImageDTO imgDto : dto.getImages()) {
                ProductImage image = new ProductImage();
                image.setImageUrl(imgDto.getImageUrl()); // Lưu chuỗi Base64
                image.setIsPrimary(imgDto.getIsPrimary());
                image.setProduct(savedProduct); // Gắn khóa ngoại
                newImages.add(image);
            }
            productImageRepository.saveAll(newImages);
        }

        return dto;
    }
    // Phân trang + tìm kiếm sản phẩm (dành cho trang Admin)
    public Page<ProductDTO> getProductsPaged(int page, int size, String keyword, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<Product> productPage = productRepository.searchProducts(kw, categoryId, pageable);

        return productPage.map(p -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setPrice(p.getPrice());
            dto.setBrand(p.getBrand());
            dto.setColor(p.getColor());
            dto.setDescription(p.getDescription());
            dto.setMaterial(p.getMaterial());
            dto.setStockQuantity(p.getStockQuantity());
            dto.setWeight(p.getWeight());
            if (p.getCategory() != null) {
                dto.setCategoryId(p.getCategory().getId());
            }
            // Chỉ lấy ảnh chính để giảm tải dữ liệu
            if (p.getImages() != null) {
                List<ProductImageDTO> imageDTOs = p.getImages().stream()
                        .filter(img -> img.getIsPrimary() != null && img.getIsPrimary() == 1)
                        .map(img -> {
                            ProductImageDTO imgDto = new ProductImageDTO();
                            imgDto.setImageUrl(img.getImageUrl());
                            imgDto.setIsPrimary(img.getIsPrimary());
                            return imgDto;
                        }).collect(Collectors.toList());
                dto.setImages(imageDTOs);
            }
            return dto;
        });
    }

    // Xóa sản phẩm (xóa cả ảnh liên quan trước)
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        // Xóa ảnh trước để tránh lỗi khóa ngoại
        productImageRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}
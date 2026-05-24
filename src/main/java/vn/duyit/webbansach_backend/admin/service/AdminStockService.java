package vn.duyit.webbansach_backend.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.dto.StockDTO;
import vn.duyit.webbansach_backend.dto.StockSummaryDTO;
import vn.duyit.webbansach_backend.dto.StockUpdateDTO;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.entity.ProductImage;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.repository.StockRepository;

@Service
public class AdminStockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    public AdminStockService(StockRepository stockRepository,
                             ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    // =============================================
    // 1. LẤY DANH SÁCH TỒN KHO (phân trang + lọc)
    // =============================================
    public Page<StockDTO> getStockList(int page, int size,
                                       String keyword, Long categoryId,
                                       String stockStatus) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("stockQuantity").ascending()); // Ưu tiên sách sắp hết lên đầu

        String kw = (keyword != null && !keyword.trim().isEmpty())
                ? keyword.trim() : null;
        String status = (stockStatus != null && !stockStatus.isEmpty())
                ? stockStatus : "all";

        Page<Product> productPage = stockRepository.findByStockFilter(
                kw, categoryId, status, pageable);

        return productPage.map(this::mapToStockDTO);
    }

    // =============================================
    // 2. THỐNG KÊ TỔNG QUAN TỒN KHO
    // =============================================
    public StockSummaryDTO getSummary() {
        long total     = stockRepository.count();
        long outOfStock = stockRepository.countOutOfStock();
        long lowStock  = stockRepository.countLowStock();
        long inStock   = stockRepository.countInStock();
        long totalQty  = stockRepository.sumTotalStock();

        return new StockSummaryDTO(total, outOfStock, lowStock, inStock, totalQty);
    }

    // =============================================
    // 3. NHẬP HÀNG / ĐIỀU CHỈNH TỒN KHO
    // =============================================
    @Transactional
    public StockDTO updateStock(StockUpdateDTO request) {
        if (request.getProductId() == null) {
            throw new RuntimeException("productId không được để trống!");
        }
        if (request.getQuantity() == null) {
            throw new RuntimeException("Số lượng không được để trống!");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy sách với ID: " + request.getProductId()));

        int oldQty = product.getStockQuantity() != null
                ? product.getStockQuantity() : 0;
        int newQty;

        if ("SET".equals(request.getType())) {
            // Đặt lại về số lượng cụ thể
            if (request.getQuantity() < 0) {
                throw new RuntimeException("Số lượng không được âm!");
            }
            newQty = request.getQuantity();
        } else {
            // ADD: cộng thêm vào tồn kho hiện tại
            newQty = oldQty + request.getQuantity();
            if (newQty < 0) {
                throw new RuntimeException(
                        "Tồn kho sau điều chỉnh không được âm! " +
                                "Hiện tại: " + oldQty + ", giảm: " + Math.abs(request.getQuantity()));
            }
        }

        product.setStockQuantity(newQty);
        Product saved = productRepository.save(product);
        return mapToStockDTO(saved);
    }

    // =============================================
    // 4. NHẬP HÀNG HÀNG LOẠT (bulk)
    // =============================================
    @Transactional
    public int bulkAddStock(java.util.List<StockUpdateDTO> requests) {
        int successCount = 0;
        for (StockUpdateDTO req : requests) {
            try {
                req.setType("ADD");
                updateStock(req);
                successCount++;
            } catch (Exception e) {
                // Bỏ qua lỗi từng item, tiếp tục xử lý
            }
        }
        return successCount;
    }

    // =============================================
    // HELPER: map Product → StockDTO
    // =============================================
    private StockDTO mapToStockDTO(Product p) {
        StockDTO dto = new StockDTO();
        dto.setProductId(p.getId());
        dto.setProductName(p.getName());
        dto.setAuthor(p.getBrand());
        dto.setPublisher(p.getMaterial());
        dto.setPrice(p.getPrice());
        dto.setStockQuantity(p.getStockQuantity() != null ? p.getStockQuantity() : 0);

        if (p.getCategory() != null) {
            dto.setCategoryName(p.getCategory().getName());
            dto.setCategoryId(p.getCategory().getId());
        }

        // Lấy ảnh bìa chính
        if (p.getImages() != null) {
            p.getImages().stream()
                    .filter(img -> img.getIsPrimary() != null && img.getIsPrimary() == 1)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .ifPresent(dto::setImageUrl);
        }

        // Tính trạng thái tồn kho
        int qty = dto.getStockQuantity();
        if (qty == 0)       dto.setStockStatus("out");
        else if (qty <= 10) dto.setStockStatus("low");
        else                dto.setStockStatus("ok");

        return dto;
    }
}
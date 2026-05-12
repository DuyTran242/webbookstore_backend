package vn.duyit.webbansach_backend.dto;
import java.math.BigDecimal;
public class CartItemResponse1 {
    private Long id; // ID của CartItem để dùng cho nút Xóa
    private Long productId;
    private String productName;
    private int quantity;
    // Đổi Double thành BigDecimal
    private BigDecimal price;
    private String imageUrl; // (Tuỳ chọn) Trả về link ảnh để Frontend hiển thị luôn

    // Các hàm Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
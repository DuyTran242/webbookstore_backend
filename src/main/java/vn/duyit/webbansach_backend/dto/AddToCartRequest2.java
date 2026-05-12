package vn.duyit.webbansach_backend.dto;

public class AddToCartRequest2 {
    private Long userId;
    private Long productId;
    private int quantity;

    // Các hàm Getters và Setters (Hoặc dùng @Data của Lombok nếu có)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
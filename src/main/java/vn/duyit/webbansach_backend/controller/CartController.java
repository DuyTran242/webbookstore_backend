package vn.duyit.webbansach_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Import đúng các DTO mới
import vn.duyit.webbansach_backend.dto.AddToCartRequest;
import vn.duyit.webbansach_backend.dto.CartItemResponse;
import vn.duyit.webbansach_backend.service.CartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. LẤY GIỎ HÀNG CỦA USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCartByUser(@PathVariable Long userId){
        List<CartItemResponse> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }

    // 2. THÊM SẢN PHẨM VÀO GIỎ HÀNG
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request){
        try {
            // Do hàm addToCart trong Service đã trả về thẳng ResponseEntity<?>
            return cartService.addToCart(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. CẬP NHẬT SỐ LƯỢNG (TĂNG/GIẢM)
    @PutMapping("/update/{itemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long itemId, @RequestParam Integer quantity) {
        try {
            cartService.updateQuantity(itemId, quantity);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. XÓA SẢN PHẨM KHỎI GIỎ HÀNG
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {
        try {
            cartService.removeCartItem(itemId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm khỏi giỏ hàng"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi khi xóa sản phẩm"));
        }
    }
}
package vn.duyit.webbansach_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.entity.Cart;
import vn.duyit.webbansach_backend.entity.CartItem;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.entity.User;
import vn.duyit.webbansach_backend.repository.CartItemRepository;
import vn.duyit.webbansach_backend.repository.CartRepository;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.repository.UserRepository;

// Import DTO
import vn.duyit.webbansach_backend.dto.AddToCartRequest;
import vn.duyit.webbansach_backend.dto.CartItemResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    private ProductImageService productImageService; // Inject Service xử lý ảnh

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Lấy hoặc tạo mới giỏ hàng cho User
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();

            // Xử lý logic gán User
            User user = userRepository.findById(userId)
                    .orElseGet(() -> {
                        User tempUser = new User();
                        tempUser.setId(userId);
                        return tempUser;
                    });

            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
    }

    // Lấy danh sách giỏ hàng (Trả về List CartItemResponse)
    public List<CartItemResponse> getCartItems(Long userId){
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        // Map List Entity sang List DTO
        return items.stream().map(item -> {
            CartItemResponse dto = new CartItemResponse();
            dto.setId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());

            // GỌI SERVICE LẤY ẢNH VÀ GÁN VÀO DTO
            String imageUrl = productImageService.getPrimaryImageUrlByProductId(item.getProduct().getId());
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());
    }

    // Thêm sản phẩm vào giỏ (Sử dụng AddToCartRequest)
    @Transactional
    public ResponseEntity<?> addToCart(AddToCartRequest request) {
        Cart cart = getOrCreateCart(request.getUserId());

        // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());

        if (existingItem.isPresent()) {
            return ResponseEntity.badRequest().body("Sản phẩm đã tồn tại trong giỏ hàng của bạn.");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Lưu vào Database
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getPrice());

        CartItem savedItem = cartItemRepository.save(item);

        // Chuyển đổi Entity sang DTO để trả về Frontend
        CartItemResponse responseDTO = new CartItemResponse();
        responseDTO.setId(savedItem.getId());
        responseDTO.setProductId(product.getId());
        responseDTO.setProductName(product.getName());
        responseDTO.setQuantity(savedItem.getQuantity());
        responseDTO.setPrice(savedItem.getPrice());

        // GỌI SERVICE LẤY ẢNH VÀ GÁN VÀO DTO (TRẢ VỀ CHO FRONTEND NGAY SAU KHI THÊM)
        String imageUrl = productImageService.getPrimaryImageUrlByProductId(product.getId());
        responseDTO.setImageUrl(imageUrl);

        return ResponseEntity.ok(responseDTO);
    }

    // Cập nhật số lượng
    @Transactional
    public void updateQuantity(Long itemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        // Nếu quantity <= 0 thì tự động xóa khỏi giỏ
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    // Xóa sản phẩm khỏi giỏ
    @Transactional
    public void removeCartItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }
    @Transactional
    public void clearCartByUserId(Long userId) {
        if (userId != null) {
            cartItemRepository.deleteByUserId(userId);
        }
    }
}
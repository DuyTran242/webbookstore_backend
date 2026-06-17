package vn.duyit.webbansach_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.dto.OrderDetailResponseDTO;
import vn.duyit.webbansach_backend.dto.OrderItemDTO;
import vn.duyit.webbansach_backend.dto.OrderRequestDTO;
import vn.duyit.webbansach_backend.dto.OrderResponseDTO;
import vn.duyit.webbansach_backend.entity.*;
import vn.duyit.webbansach_backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    // Inject các Repository cần thiết
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ShippingRepository shippingRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartService cartService;

    @Autowired private UserRepository userRepository;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private DiscountCodeService discountCodeService;


    // Hàm lấy danh sách đơn hàng của người dùng
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional
    public Order createOrder(OrderRequestDTO request) {
        String fullAddress = request.getAddress() + ", " +
                request.getWard() + ", " +
                request.getDistrict() + ", " +
                request.getProvince();

        Order order = new Order();

        // SỬA LẠI ĐOẠN NÀY: Lấy User entity từ DB và set vào Order
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
            order.setUser(user);
        }

        order.setPhone(request.getPhone());
        order.setShippingAddress(fullAddress);
        order.setShippingFee(request.getShippingFee());

        BigDecimal subtotal = BigDecimal.ZERO;
        if (request.getItems() != null) {
            for (OrderRequestDTO.OrderItemDTO itemDto : request.getItems()) {
                subtotal = subtotal.add(itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            }
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getDiscountCode() != null && !request.getDiscountCode().isEmpty()) {
            try {
                discountAmount = discountCodeService.calculateDiscount(request.getDiscountCode(), subtotal);
                discountCodeService.incrementUsedCount(request.getDiscountCode());
            } catch (Exception e) {
                System.out.println("Lỗi áp dụng mã giảm giá: " + e.getMessage());
            }
        }

        order.setDiscount(discountAmount);
        
        // Recalculate total price to be safe
        BigDecimal finalTotal = subtotal.add(request.getShippingFee()).subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }
        order.setTotalPrice(finalTotal);
        order.setNote(request.getNote());
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        order = orderRepository.save(order);

        // 3. LƯU VÀO BẢNG order_items (Đã sửa lại để set Product thay vì ID)
        if (request.getItems() != null) {
            for (OrderRequestDTO.OrderItemDTO itemDto : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrder(order); // Set đối tượng Order

                // TÌM PRODUCT ENTITY TỪ DATABASE
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại ID: " + itemDto.getProductId()));

                item.setProduct(product); // ĐÃ SỬA CHỖ NÀY: Set đối tượng Product
                item.setQuantity(itemDto.getQuantity());
                item.setPrice(itemDto.getPrice());
                orderItemRepository.save(item);
            }
        }

        // 4. Lưu vào bảng shipping
        Shipping shipping = new Shipping();
        shipping.setOrder(order); // Tương tự: set đối tượng Order nếu Shipping entity map object
        shipping.setShippingProvider("Giao Hàng Nhanh");
        shipping.setShippingFee(request.getShippingFee());
        shipping.setShippingStatus("WAITING_FOR_PICKUP");
        shipping.setCreatedAt(LocalDateTime.now());
        shippingRepository.save(shipping);

        // 5. Lưu vào bảng payments
        Payment payment = new Payment();
        payment.setOrder(order); // Tương tự: set đối tượng Order
        payment.setMethod(request.getPaymentMethod());
        payment.setAmount(request.getTotalPrice());
        payment.setCreatedAt(LocalDateTime.now());

        if ("VNPAY".equals(request.getPaymentMethod())) {
            payment.setStatus("UNPAID");
        } else {
            payment.setStatus("PENDING");
        }
        paymentRepository.save(payment);

        // 6. Xóa giỏ hàng
        if (request.getUserId() != null) {
            cartService.clearCartByUserId(request.getUserId());
        }

        return order;
    }

    // Hàm cập nhật trạng thái thanh toán VNPAY và trừ kho (gọi từ OrderController khi Callback)
    @Transactional
    public void updatePaymentStatus(Long orderId, String status, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));

        if ("SUCCESS".equals(status)) {
            // 1. Cập nhật bảng orders
            order.setStatus("PROCESSING");
            order.setPaymentStatus("PAID");
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // 2. Cập nhật bảng payments
            Payment payment = paymentRepository.findByOrder_Id(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho đơn: " + orderId));
            payment.setStatus("SUCCESS");
            payment.setTransactionId(transactionId);
            paymentRepository.save(payment);

            // 3. Xử lý trừ kho trong bảng products
            // Lưu ý: Nếu trong OrderItem bạn khai báo private Order order;
// thì hàm này bên repository phải là findByOrder_Id(orderId) nhé!
            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

            for (OrderItem item : items) {
                // SỬA Ở ĐÂY: Lấy ID từ đối tượng Product bên trong OrderItem
                Long productId = item.getProduct().getId();

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + productId));

                // Kiểm tra xem số lượng tồn kho có đủ không
                if (product.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " đã hết hàng!");
                }

                // Thực hiện trừ số lượng
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                productRepository.save(product);
            }


        } else {
            // Trường hợp thanh toán thất bại hoặc bị hủy
            order.setPaymentStatus("FAILED");
            orderRepository.save(order);

            Payment payment = paymentRepository.findByOrder_Id(orderId).orElse(null);
            if (payment != null) {
                payment.setStatus("FAILED");
                payment.setTransactionId(transactionId);
                paymentRepository.save(payment);
            }
        }
    }
    // Lấy danh sách lịch sử đơn hàng
    public List<OrderResponseDTO> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(order -> {
            OrderResponseDTO dto = new OrderResponseDTO();
            dto.setId(order.getId());
            dto.setCreatedAt(order.getCreatedAt());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setStatus(order.getStatus());
            dto.setPaymentStatus(order.getPaymentStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    // Lấy chi tiết 1 đơn hàng
    public OrderDetailResponseDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        OrderDetailResponseDTO dto = new OrderDetailResponseDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPhone(order.getPhone());
        dto.setNote(order.getNote());

        // Map danh sách sản phẩm
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(item -> {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            String imageUrl = productImageService.getPrimaryImageUrlByProductId(item.getProduct().getId());
            itemDto.setImageUrl(imageUrl);
            // Thay đổi tùy theo logic lưu ảnh của bạn
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }
}
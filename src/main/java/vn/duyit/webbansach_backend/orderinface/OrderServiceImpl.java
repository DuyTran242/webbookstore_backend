package vn.duyit.webbansach_backend.orderinface;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.OrderDTO;
import vn.duyit.webbansach_backend.dto.OrderDetailDTO;
import vn.duyit.webbansach_backend.dto.OrderItemDetailDTO;
import vn.duyit.webbansach_backend.entity.Order;
import vn.duyit.webbansach_backend.entity.ProductImage;
import vn.duyit.webbansach_backend.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<OrderDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        return orders.stream().map(this::mapToOrderDTO).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public OrderDetailDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "Khách vãng lai"); // Thay đổi tùy theo entity User của bạn
        dto.setPhone(order.getPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemDetailDTO> items = order.getOrderItems().stream().map(item -> {
            OrderItemDetailDTO itemDTO = new OrderItemDetailDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setQuantity(item.getQuantity());

            // Lấy ảnh đại diện (isPrimary = 1)
            String imgUrl = item.getProduct().getImages().stream()
                    .filter(img -> img.getIsPrimary() != null && img.getIsPrimary() == 1)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(null);
            itemDTO.setImageUrl(imgUrl);

            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "Khách");
        dto.setPhone(order.getPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
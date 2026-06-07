package vn.duyit.webbansach_backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.admin.dto.OrderAdminDTO;
import vn.duyit.webbansach_backend.admin.repository.OrderAdminRepository;
import vn.duyit.webbansach_backend.admin.dto.OrderSummaryDTO;
import vn.duyit.webbansach_backend.admin.dto.OrderAdminDetailDTO;
import vn.duyit.webbansach_backend.dto.*;
import vn.duyit.webbansach_backend.entity.*;
import vn.duyit.webbansach_backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderAdminRepository orderAdminRepository;
    private final OrderItemRepository  orderItemRepository;
    private final ShippingRepository   shippingRepository;
    private final PaymentRepository    paymentRepository;

    // 1. DANH SÁCH ĐƠN HÀNG (phân trang + tìm kiếm + lọc)

    public Page<OrderAdminDTO> getOrders(
            int page, int size,
            String keyword, String status, String payStatus,
            String fromDateStr, String toDateStr) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending());

        // Chuẩn hóa tham số
        String kw  = (keyword   != null && !keyword.trim().isEmpty())   ? keyword.trim() : null;
        String st  = (status    != null && !status.equals("all"))        ? status         : null;
        String pst = (payStatus != null && !payStatus.equals("all"))     ? payStatus      : null;

        LocalDateTime from = null, to = null;
        try {
            if (fromDateStr != null && !fromDateStr.isEmpty())
                from = LocalDate.parse(fromDateStr).atStartOfDay();
            if (toDateStr   != null && !toDateStr.isEmpty())
                to   = LocalDate.parse(toDateStr).atTime(23, 59, 59);
        } catch (Exception ignored) {}

        Page<Order> orderPage = orderAdminRepository.searchOrders(
                kw, st, pst, from, to, pageable);

        return orderPage.map(this::mapToListDTO);
    }

    // 2. THỐNG KÊ TỔNG QUAN
    public OrderSummaryDTO getSummary() {
        return new OrderSummaryDTO(
                orderAdminRepository.count(),
                orderAdminRepository.countByStatus("PENDING"),
                orderAdminRepository.countByStatus("PROCESSING"),
                orderAdminRepository.countByStatus("SHIPPED"),
                orderAdminRepository.countByStatus("DELIVERED"),
                orderAdminRepository.countByStatus("CANCELLED"),
                orderAdminRepository.countByPaymentStatus("UNPAID"),
                orderAdminRepository.sumRevenuePaid()
        );
    }

    // 3. CHI TIẾT ĐƠN HÀNG
    public OrderAdminDetailDTO getOrderDetail(Long orderId) {
        Order order = orderAdminRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy đơn hàng #" + orderId));

        OrderAdminDetailDTO dto = new OrderAdminDetailDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setPhone(order.getPhone());
        dto.setShippingAddress(order.getShippingAddress());

        // Thông tin khách hàng
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setCustomerName(order.getUser().getFullName());
            dto.setCustomerEmail(order.getUser().getEmail());
        } else {
            dto.setCustomerName("Khách vãng lai");
        }

        // Thông tin vận chuyển
        if (order.getShipping() != null) {
            dto.setShippingProvider(order.getShipping().getShippingProvider());
            dto.setTrackingCode(order.getShipping().getTrackingCode());
            dto.setShippingStatus(order.getShipping().getShippingStatus());
        }

        // Thông tin thanh toán
        if (order.getPayment() != null) {
            dto.setPaymentMethod(order.getPayment().getMethod());
            dto.setTransactionId(order.getPayment().getTransactionId());
        }

        // Danh sách sách
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        dto.setItems(items.stream().map(item -> {
            OrderAdminDetailDTO.OrderAdminItemDTO i = new OrderAdminDetailDTO.OrderAdminItemDTO();
            i.setProductId(item.getProduct().getId());
            i.setProductName(item.getProduct().getName());
            i.setAuthor(item.getProduct().getBrand());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setSubtotal(item.getPrice().multiply(
                    BigDecimal.valueOf(item.getQuantity())));
            // Lấy ảnh bìa chính
            if (item.getProduct().getImages() != null) {
                item.getProduct().getImages().stream()
                        .filter(img -> img.getIsPrimary() != null && img.getIsPrimary() == 1)
                        .map(ProductImage::getImageUrl)
                        .findFirst()
                        .ifPresent(i::setImageUrl);
            }
            return i;
        }).collect(Collectors.toList()));

        return dto;
    }

    // 4. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    @Transactional
    public OrderAdminDTO updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderAdminRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy đơn hàng #" + orderId));

        // Kiểm tra chuyển trạng thái hợp lệ
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        // Cập nhật trạng thái vận chuyển theo đơn
        if (order.getShipping() != null) {
            String shippingStatus = mapOrderStatusToShipping(newStatus);
            if (shippingStatus != null) {
                order.getShipping().setShippingStatus(shippingStatus);
                shippingRepository.save(order.getShipping());
            }
        }

        return mapToListDTO(orderAdminRepository.save(order));
    }

    // 5. HỦY ĐƠN HÀNG (có hoàn kho)
    @Transactional
    public OrderAdminDTO cancelOrder(Long orderId, String reason) {
        Order order = orderAdminRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy đơn hàng #" + orderId));

        // Chỉ cho hủy khi PENDING hoặc PROCESSING
        if (!List.of("PENDING", "PROCESSING").contains(order.getStatus())) {
            throw new RuntimeException(
                    "Không thể hủy đơn hàng ở trạng thái: " + order.getStatus());
        }

        // Nếu đã thanh toán VNPAY thì hoàn lại kho
        if ("PAID".equals(order.getPaymentStatus())) {
            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
            for (OrderItem item : items) {
                Product product = item.getProduct();
                product.setStockQuantity(
                        product.getStockQuantity() + item.getQuantity());
            }
        }

        order.setStatus("CANCELLED");
        order.setNote((order.getNote() != null ? order.getNote() + " | " : "")
                + "Lý do hủy: " + (reason != null ? reason : "Admin hủy"));
        order.setUpdatedAt(LocalDateTime.now());

        return mapToListDTO(orderAdminRepository.save(order));
    }

    // HELPER: Kiểm tra chuyển trạng thái hợp lệ
    private void validateStatusTransition(String current, String next) {
        // Không cho phép chuyển từ DELIVERED hoặc CANCELLED sang trạng thái khác
        if ("DELIVERED".equals(current) || "CANCELLED".equals(current)) {
            throw new RuntimeException(
                    "Không thể thay đổi trạng thái đơn hàng đã " +
                            ("DELIVERED".equals(current) ? "giao thành công" : "hủy"));
        }
    }

    private String mapOrderStatusToShipping(String orderStatus) {
        return switch (orderStatus) {
            case "PROCESSING" -> "WAITING_FOR_PICKUP";
            case "SHIPPED"    -> "IN_TRANSIT";
            case "DELIVERED"  -> "DELIVERED";
            case "CANCELLED"  -> "CANCELLED";
            default -> null;
        };
    }

    // HELPER: map Order entity → OrderAdminDTO
    private OrderAdminDTO mapToListDTO(Order order) {
        OrderAdminDTO dto = new OrderAdminDTO();
        dto.setId(order.getId());
        dto.setPhone(order.getPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setShippingFee(order.getShippingFee());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItemCount(order.getOrderItems() != null
                ? order.getOrderItems().size() : 0);

        if (order.getUser() != null) {
            dto.setCustomerName(order.getUser().getFullName());
            dto.setCustomerEmail(order.getUser().getEmail());
        } else {
            dto.setCustomerName("Khách vãng lai");
        }

        if (order.getPayment() != null) {
            dto.setPaymentMethod(order.getPayment().getMethod());
        }

        return dto;
    }
}

package vn.duyit.webbansach_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.OrderRequestDTO;
import vn.duyit.webbansach_backend.entity.Order;
import vn.duyit.webbansach_backend.service.OrderService;
import vn.duyit.webbansach_backend.vnpay.VNPayService;
import vn.duyit.webbansach_backend.dto.OrderResponseDTO;
import vn.duyit.webbansach_backend.dto.OrderDetailResponseDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private VNPayService vnpayService;

    // 1. Lấy danh sách đơn hàng của User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // 2. Tạo đơn hàng (Hỗ trợ cả COD và VNPAY)
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO dto, HttpServletRequest request) {
        try {
            // Lưu đơn hàng xuống DB (4 bảng: orders, order_items, shipping, payments)
            Order newOrder = orderService.createOrder(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", newOrder.getId());

            if ("VNPAY".equals(dto.getPaymentMethod())) {
                // Nếu là VNPAY, tạo URL thanh toán
                long amount = newOrder.getTotalPrice().longValue();
                String paymentUrl = vnpayService.createPaymentUrl(newOrder.getId(), amount, request);
                response.put("paymentUrl", paymentUrl);
            } else {
                response.put("message", "Đơn hàng COD đã được tạo thành công");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi tạo đơn hàng: " + e.getMessage());
        }
    }

    /**
     * 3. API xử lý kết quả trả về từ VNPAY (vnp_ReturnUrl)
     * VNPAY sẽ redirect khách hàng về URL này sau khi thanh toán xong.
     * Chúng ta sẽ cập nhật DB và redirect tiếp khách hàng về giao diện React (Frontend).
     */
    @GetMapping("/vnpay-payment-return")
    public void vnpayPaymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy các tham số VNPAY trả về trên URL
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String orderIdStr = request.getParameter("vnp_TxnRef");
        String transactionId = request.getParameter("vnp_TransactionNo");

        // Địa chỉ trang kết quả thanh toán ở Frontend (React)
        String frontendUrl = "http://localhost:3000/payment-status";

        try {
            if (orderIdStr == null || orderIdStr.isEmpty()) {
                response.sendRedirect(frontendUrl + "?status=error&message=Invalid_Order");
                return;
            }

            Long orderId = Long.parseLong(orderIdStr);

            // "00" là mã thành công của VNPAY
            if ("00".equals(vnp_ResponseCode)) {
                // Cập nhật trạng thái PAID, PROCESSING và TRỪ KHO
                orderService.updatePaymentStatus(orderId, "SUCCESS", transactionId);

                // Redirect khách về trang thành công của React
                response.sendRedirect(frontendUrl + "?status=success&orderId=" + orderId);
            } else {
                // Thanh toán thất bại hoặc khách hủy
                orderService.updatePaymentStatus(orderId, "FAILED", transactionId);

                // Redirect khách về trang thất bại của React
                response.sendRedirect(frontendUrl + "?status=failed&vnp_Code=" + vnp_ResponseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi hệ thống (như hết hàng khi trừ kho), gửi thông báo lỗi về Frontend
            response.sendRedirect(frontendUrl + "?status=error&message=" + e.getMessage());
        }
    }
    // Lấy danh sách đơn hàng theo User ID
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId));
    }

    // Lấy chi tiết đơn hàng
    @GetMapping("/{id}/details")
    public ResponseEntity<OrderDetailResponseDTO> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }
}
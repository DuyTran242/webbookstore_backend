package vn.duyit.webbansach_backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.dto.OrderAdminDTO;
import vn.duyit.webbansach_backend.admin.dto.OrderSummaryDTO;
import vn.duyit.webbansach_backend.admin.service.AdminOrderService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // Danh sách đơn hàng — phân trang + tìm kiếm + lọc
    @GetMapping
    public ResponseEntity<Page<OrderAdminDTO>> getOrders(
            @RequestParam(defaultValue = "0")   int    page,
            @RequestParam(defaultValue = "15")  int    size,
            @RequestParam(required = false)     String keyword,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String payStatus,
            @RequestParam(required = false)     String fromDate,
            @RequestParam(required = false)     String toDate
    ) {
        Page<OrderAdminDTO> result = adminOrderService.getOrders(
                page, size, keyword, status, payStatus, fromDate, toDate);
        return ResponseEntity.ok(result);
    }

    // Thống kê tổng quan đơn hàng
    @GetMapping("/summary")
    public ResponseEntity<OrderSummaryDTO> getSummary() {
        return ResponseEntity.ok(adminOrderService.getSummary());
    }

    // Chi tiết một đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminOrderService.getOrderDetail(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Cập nhật trạng thái đơn hàng

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String newStatus = body.get("status");
            if (newStatus == null || newStatus.isBlank())
                return ResponseEntity.badRequest().body("Trạng thái không được để trống!");
            OrderAdminDTO updated = adminOrderService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật trạng thái thành công",
                    "order",   updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Hủy đơn hàng (có hoàn kho nếu cần)
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String reason = (body != null) ? body.get("reason") : null;
            OrderAdminDTO updated = adminOrderService.cancelOrder(id, reason);
            return ResponseEntity.ok(Map.of(
                    "message", "Đã hủy đơn hàng thành công",
                    "order",   updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
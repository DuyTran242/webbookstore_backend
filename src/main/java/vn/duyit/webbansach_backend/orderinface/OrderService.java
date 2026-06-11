package vn.duyit.webbansach_backend.orderinface;

import vn.duyit.webbansach_backend.dto.OrderDTO;
import vn.duyit.webbansach_backend.dto.OrderDetailDTO;

import java.util.List;

public interface OrderService {
    // Lấy danh sách đơn hàng theo trạng thái
    List<OrderDTO> getOrdersByStatus(String status);

    // Lấy chi tiết một đơn hàng dựa vào id
    OrderDetailDTO getOrderDetails(Long id);

    // Cập nhật trạng thái của đơn hàng
    void updateOrderStatus(Long id, String newStatus);
}

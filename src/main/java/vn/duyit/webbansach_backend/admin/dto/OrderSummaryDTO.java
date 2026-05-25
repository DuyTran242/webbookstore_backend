package vn.duyit.webbansach_backend.admin.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

//Thẻ thống kê tổng quan đơn hàng
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDTO {
    private long totalOrders;
    private long pending;       // Chờ xử lý
    private long processing;    // Đang xử lý
    private long shipped;       // Đang giao
    private long delivered;     // Đã giao
    private long cancelled;     // Đã hủy
    private long unpaid;        // Chưa thanh toán
    private BigDecimal totalRevenue; // Tổng doanh thu từ đơn PAID
}

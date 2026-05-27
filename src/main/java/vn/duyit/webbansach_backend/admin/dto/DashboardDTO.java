package vn.duyit.webbansach_backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardDTO {

    //Thẻ KPI tổng quan
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class KpiDTO {
        private BigDecimal totalRevenue;        // Tổng doanh thu
        private BigDecimal revenueThisMonth;    // DT tháng này
        private BigDecimal revenueLastMonth;    // DT tháng trước
        private double     revenueGrowth;       // % tăng trưởng DT

        private long totalOrders;               // Tổng đơn hàng
        private long ordersThisMonth;           // Đơn tháng này
        private long pendingOrders;             // Đơn chờ xử lý
        private long shippedOrders;             // Đang giao

        private long totalUsers;                // Tổng người dùng
        private long newUsersThisMonth;         // User mới tháng này

        private long totalProducts;             // Tổng đầu sách
        private long outOfStockProducts;        // Sách hết hàng
    }

    //Điểm dữ liệu trên biểu đồ
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class ChartPointDTO {
        private String     label;
        private BigDecimal revenue;
        private long       orders;
        private long       users;
    }

    //Top sách bán chạy
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TopBookDTO {
        private Long       productId;
        private String     productName;
        private String     author;
        private String     imageUrl;
        private long       soldQty;
        private BigDecimal revenue;
    }

    //  Top danh mục
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TopCategoryDTO {
        private String     categoryName;
        private long       soldQty;
        private BigDecimal revenue;
        private double     percentage;
    }

    //ư Response tổng hợp cho dashboard ư
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DashboardResponse {
        private KpiDTO               kpi;
        private List<ChartPointDTO>  chartData;      // 30 ngày / 12 tháng
        private List<TopBookDTO>     topBooks;        // Top 5 sách bán chạy
        private List<TopCategoryDTO> topCategories;   // Top 5 danh mục
        private String               period;          // "30days" | "12months"
    }
}
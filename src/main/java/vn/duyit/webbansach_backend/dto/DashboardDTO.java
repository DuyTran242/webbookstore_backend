package vn.duyit.webbansach_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DashboardDTO {
    private BigDecimal totalRevenue;
    private BigDecimal lastMonthTotalRevenue; // to calculate "không đổi" or "+X%"
    private BigDecimal thisMonthRevenue;
    private BigDecimal lastMonthRevenue;
    private long totalOrders;
    private long thisMonthOrders;
    private long pendingOrders;
    private long deliveringOrders;
    private long totalUsers;
    private long newUsersThisMonth;
    private long totalBooks;
    
    private java.util.List<ChartData> revenueChartData;
    private java.util.List<ChartData> orderChartData;

    @Data
    public static class ChartData {
        private String name;
        private BigDecimal value;
        private long count;
    }
}

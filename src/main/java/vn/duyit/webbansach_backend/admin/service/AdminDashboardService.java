package vn.duyit.webbansach_backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.admin.dto.DashboardDTO;
import vn.duyit.webbansach_backend.entity.ProductImage;
import vn.duyit.webbansach_backend.admin.repository.DashboardRepository;
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.repository.ProductImageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final DashboardRepository    dashboardRepository;
    private final ProductRepository      productRepository;
    private final ProductImageRepository productImageRepository;

    public DashboardDTO.DashboardResponse getDashboard(String period) {
        DashboardDTO.DashboardResponse resp = new DashboardDTO.DashboardResponse();
        resp.setPeriod(period);
        resp.setKpi(buildKpi());
        resp.setChartData(buildChartData(period));
        resp.setTopBooks(buildTopBooks(period));
        resp.setTopCategories(buildTopCategories());
        return resp;
    }

    private DashboardDTO.KpiDTO buildKpi() {
        DashboardDTO.KpiDTO kpi = new DashboardDTO.KpiDTO();

        // Doanh thu
        kpi.setTotalRevenue(dashboardRepository.totalRevenue());
        kpi.setRevenueThisMonth(dashboardRepository.revenueThisMonth());

        // Tháng trước
        LocalDateTime lastMonthStart = LocalDate.now()
                .minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastMonthEnd = LocalDate.now()
                .withDayOfMonth(1).atStartOfDay();
        BigDecimal lastMonth = dashboardRepository.revenueBetween(lastMonthStart, lastMonthEnd);
        kpi.setRevenueLastMonth(lastMonth);

        // % tăng trưởng doanh thu
        BigDecimal thisMonth = kpi.getRevenueThisMonth();
        if (lastMonth != null && lastMonth.compareTo(BigDecimal.ZERO) > 0) {
            double growth = thisMonth.subtract(lastMonth)
                    .divide(lastMonth, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            kpi.setRevenueGrowth(Math.round(growth * 10.0) / 10.0);
        }

        // Đơn hàng
        kpi.setTotalOrders(dashboardRepository.count());
        kpi.setOrdersThisMonth(dashboardRepository.countThisMonth());
        kpi.setPendingOrders(dashboardRepository.countByStatus("PENDING"));
        kpi.setShippedOrders(dashboardRepository.countByStatus("SHIPPED"));

        // Người dùng
        kpi.setTotalUsers(dashboardRepository.totalUsers());
        kpi.setNewUsersThisMonth(dashboardRepository.newUsersThisMonth());

        // Sản phẩm
        kpi.setTotalProducts(productRepository.count());
        long outOfStock = productRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() == 0)
                .count();
        kpi.setOutOfStockProducts(outOfStock);

        return kpi;
    }

    private List<DashboardDTO.ChartPointDTO> buildChartData(String period) {
        if ("12months".equals(period)) {
            return buildMonthlyChart();
        }
        return buildDailyChart(30); // mặc định 30 ngày
    }

    private List<DashboardDTO.ChartPointDTO> buildDailyChart(int days) {
        LocalDateTime to   = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime from = LocalDate.now().minusDays(days - 1).atStartOfDay();

        // Lấy dữ liệu từ DB
        Map<String, BigDecimal> revenueMap = toStringMap(
                dashboardRepository.revenueByDay(from, to));
        Map<String, Long> orderMap = toLongMap(
                dashboardRepository.orderCountByDay(from, to));
        Map<String, Long> userMap  = toLongMap(
                dashboardRepository.newUsersByDay(from, to));

        // Tạo đủ 30 điểm kể cả ngày không có đơn
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        DateTimeFormatter key = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<DashboardDTO.ChartPointDTO> result = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date  = LocalDate.now().minusDays(i);
            String    keyStr = date.format(key);
            result.add(new DashboardDTO.ChartPointDTO(
                    date.format(fmt),
                    revenueMap.getOrDefault(keyStr, BigDecimal.ZERO),
                    orderMap.getOrDefault(keyStr, 0L),
                    userMap.getOrDefault(keyStr,  0L)
            ));
        }
        return result;
    }

    private List<DashboardDTO.ChartPointDTO> buildMonthlyChart() {
        int year = LocalDate.now().getYear();
        Map<Integer, BigDecimal> revenueMap = new HashMap<>();
        for (Object[] row : dashboardRepository.revenueByMonth(year)) {
            revenueMap.put(((Number) row[0]).intValue(),
                    new BigDecimal(row[1].toString()));
        }

        String[] monthLabels = {"T1","T2","T3","T4","T5","T6",
                "T7","T8","T9","T10","T11","T12"};
        List<DashboardDTO.ChartPointDTO> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(new DashboardDTO.ChartPointDTO(
                    monthLabels[m - 1],
                    revenueMap.getOrDefault(m, BigDecimal.ZERO),
                    0L, 0L
            ));
        }
        return result;
    }

    private List<DashboardDTO.TopBookDTO> buildTopBooks(String period) {
        LocalDateTime from = "12months".equals(period)
                ? LocalDate.now().minusYears(1).atStartOfDay()
                : LocalDate.now().minusDays(30).atStartOfDay();

        List<Object[]> rows = dashboardRepository.topSellingBooks(
                from, PageRequest.of(0, 5));

        return rows.stream().map(row -> {
            Long   productId   = ((Number) row[0]).longValue();
            String productName = (String) row[1];
            String author      = (String) row[2];
            long   soldQty     = ((Number) row[3]).longValue();
            BigDecimal revenue = new BigDecimal(row[4].toString());

            // Lấy ảnh bìa
            String imageUrl = null;
            ProductImage img = productImageRepository
                    .findFirstByProductIdAndIsPrimary(productId, 1);
            if (img != null) imageUrl = img.getImageUrl();

            return new DashboardDTO.TopBookDTO(
                    productId, productName, author, imageUrl, soldQty, revenue);
        }).collect(Collectors.toList());
    }

    private List<DashboardDTO.TopCategoryDTO> buildTopCategories() {
        List<Object[]> rows = dashboardRepository.topCategories(PageRequest.of(0, 5));

        long totalQty = rows.stream()
                .mapToLong(r -> ((Number) r[1]).longValue())
                .sum();

        return rows.stream().map(row -> {
            String     name    = (String) row[0];
            long       qty     = ((Number) row[1]).longValue();
            BigDecimal revenue = new BigDecimal(row[2].toString());
            double     pct     = totalQty > 0
                    ? Math.round((qty * 100.0 / totalQty) * 10.0) / 10.0 : 0;
            return new DashboardDTO.TopCategoryDTO(name, qty, revenue, pct);
        }).collect(Collectors.toList());
    }

    private Map<String, BigDecimal> toStringMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] r : rows) {
            map.put(r[0].toString(), new BigDecimal(r[1].toString()));
        }
        return map;
    }

    private Map<String, Long> toLongMap(List<Object[]> rows) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] r : rows) {
            map.put(r[0].toString(), ((Number) r[1]).longValue());
        }
        return map;
    }
}

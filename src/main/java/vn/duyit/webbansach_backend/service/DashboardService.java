package vn.duyit.webbansach_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.DashboardDTO;
import vn.duyit.webbansach_backend.repository.OrderRepository;
import vn.duyit.webbansach_backend.repository.UserRepository;
import vn.duyit.webbansach_backend.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public DashboardDTO getDashboardSummary(String period) {
        DashboardDTO dto = new DashboardDTO();

        // Revenue stats
        dto.setTotalRevenue(orZero(orderRepository.calculateTotalPaidRevenue()));
        dto.setThisMonthRevenue(orZero(orderRepository.calculateThisMonthPaidRevenue()));
        dto.setLastMonthRevenue(orZero(orderRepository.calculateLastMonthPaidRevenue()));
        dto.setLastMonthTotalRevenue(orZero(orderRepository.calculateLastMonthPaidRevenue()));

        // Order stats
        dto.setTotalOrders(orderRepository.countTotalValidOrders());
        dto.setThisMonthOrders(orderRepository.countThisMonthValidOrders());
        dto.setPendingOrders(orderRepository.countPendingOrders());
        dto.setDeliveringOrders(orderRepository.countDeliveringOrders());

        // User stats
        dto.setTotalUsers(userRepository.countActiveUsers());
        dto.setNewUsersThisMonth(userRepository.countNewUsersThisMonth());

        // Product stats
        dto.setTotalBooks(productRepository.countInStockProducts());

        // Charts - switch based on period
        List<Object[]> chartRaw;
        boolean isMonthly = "12months".equals(period);

        if (isMonthly) {
            chartRaw = orderRepository.getMonthlyRevenueAndOrdersLast12Months();
        } else {
            chartRaw = orderRepository.getDailyRevenueAndOrdersLast30Days();
        }

        List<DashboardDTO.ChartData> revenueChart = new java.util.ArrayList<>();
        List<DashboardDTO.ChartData> orderChart = new java.util.ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (Object[] row : chartRaw) {
            String label;
            if (isMonthly) {
                // For monthly query: row[0] is already formatted as 'mm/yyyy'
                label = row[0] != null ? row[0].toString() : "";
            } else {
                // For daily query: row[0] is a date, need formatting
                if (row[0] instanceof java.sql.Date) {
                    label = ((java.sql.Date) row[0]).toLocalDate().format(formatter);
                } else if (row[0] instanceof LocalDate) {
                    label = ((LocalDate) row[0]).format(formatter);
                } else if (row[0] != null) {
                    try {
                        label = LocalDate.parse(row[0].toString()).format(formatter);
                    } catch (Exception e) {
                        label = row[0].toString();
                    }
                } else {
                    label = "";
                }
            }

            BigDecimal rev = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
            long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            DashboardDTO.ChartData rData = new DashboardDTO.ChartData();
            rData.setName(label);
            rData.setValue(rev);
            revenueChart.add(rData);

            DashboardDTO.ChartData oData = new DashboardDTO.ChartData();
            oData.setName(label);
            oData.setCount(count);
            orderChart.add(oData);
        }

        dto.setRevenueChartData(revenueChart);
        dto.setOrderChartData(orderChart);

        return dto;
    }

    private BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

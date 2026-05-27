package vn.duyit.webbansach_backend.admin.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.admin.service.AdminDashboardService;
import vn.duyit.webbansach_backend.admin.dto.DashboardDTO;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // GET /api/admin/dashboard?period=30days   → 30 ngày gần nhất
    // GET /api/admin/dashboard?period=12months → 12 tháng trong năm
    @GetMapping
    public ResponseEntity<DashboardDTO.DashboardResponse> getDashboard(
            @RequestParam(defaultValue = "30days") String period
    ) {
        return ResponseEntity.ok(adminDashboardService.getDashboard(period));
    }
}
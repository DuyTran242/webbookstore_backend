package vn.duyit.webbansach_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.dto.DashboardDTO;
import vn.duyit.webbansach_backend.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardDTO> getSummary(@RequestParam(defaultValue = "30days") String period) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(period));
    }

}
package vn.duyit.webbansach_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    @GetMapping("/revenue")
    public String revenue(){
        return "Revenue statistics API";
    }

}
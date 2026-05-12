package vn.duyit.webbansach_backend.controller;

import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.entity.RecommendationLog;
import vn.duyit.webbansach_backend.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@CrossOrigin
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public List<RecommendationLog> getLogs(@PathVariable Long userId){
        return service.getLogs(userId);
    }

}

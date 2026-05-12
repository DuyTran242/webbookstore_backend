package vn.duyit.webbansach_backend.service;


import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.entity.RecommendationLog;
import vn.duyit.webbansach_backend.repository.RecommendationLogRepository;

import java.util.List;

@Service
public class RecommendationService {

    private final RecommendationLogRepository repository;

    public RecommendationService(RecommendationLogRepository repository) {
        this.repository = repository;
    }

    public List<RecommendationLog> getLogs(Long userId){
        return repository.findByUserId(userId);
    }

}
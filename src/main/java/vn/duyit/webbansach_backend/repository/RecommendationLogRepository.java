package vn.duyit.webbansach_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.duyit.webbansach_backend.entity.RecommendationLog;

import java.util.List;

public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, Long> {

    List<RecommendationLog> findByUserId(Long userId);

}
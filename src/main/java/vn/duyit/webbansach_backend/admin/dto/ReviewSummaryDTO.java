package vn.duyit.webbansach_backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryDTO {
    private long  totalReviews;
    private double avgRating;
    private Map<Integer, Long> ratingDistribution; // { 5: 120, 4: 80, 3: 30, 2: 10, 1: 5 }
}

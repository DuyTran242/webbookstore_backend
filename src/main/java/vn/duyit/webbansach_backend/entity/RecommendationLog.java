package vn.duyit.webbansach_backend.entity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_logs")
@Getter
@Setter
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Float score;

    private String algorithm;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
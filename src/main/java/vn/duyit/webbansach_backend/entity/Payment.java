package vn.duyit.webbansach_backend.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    private String method;

    @Column(name = "transaction_id")
    private String transactionId;

    private BigDecimal amount;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

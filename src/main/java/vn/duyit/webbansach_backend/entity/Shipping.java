package vn.duyit.webbansach_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "shipping")
@Data
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @Column(name = "shipping_provider")
    private String shippingProvider;

    @Column(name = "tracking_code")
    private String trackingCode;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "shipping_status")
    private String shippingStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
package vn.duyit.webbansach_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "order_items")
@Getter @Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    private Integer quantity;

    private BigDecimal price;
    @Column(name = "serial_id")
    private Long serialId; // Lưu ID của ProductSerial đã bán

    @Column(name = "warranty_start_date")
    private LocalDateTime warrantyStartDate;

    @Column(name = "warranty_end_date")
    private LocalDateTime warrantyEndDate;
}

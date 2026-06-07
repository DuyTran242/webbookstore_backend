package vn.duyit.webbansach_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_serials")
@Getter
@Setter
public class ProductSerial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;

    // Trạng thái: IN_STOCK, SOLD, DEFECTIVE
    private String status;
}

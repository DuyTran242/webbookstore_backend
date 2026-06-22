package vn.duyit.webbansach_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String brand;

    private String material;

    private String color;

    private Double weight;

    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "has_serial", columnDefinition = "boolean default false")
    private Boolean hasSerial;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<ProductReview> reviews;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<ProductView> views;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;


    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<CartItem> cartItems;

    @Column(name = "import_price")
    private Double importPrice;

    private String supplier;
}
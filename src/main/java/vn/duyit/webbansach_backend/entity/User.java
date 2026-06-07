package vn.duyit.webbansach_backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    @Column(name = "full_name")
    private String fullName;

    private String phone;

    private String address;
    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "gender")
    private String gender;
    @Lob

    @Column(name = "avatar", columnDefinition = "LONGTEXT")
    private String avatar;

    private String provider;

    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<ProductReview> productReviews;

    @OneToMany(mappedBy = "user")
    private List<ProductView> productViews;

    @Column(name = "verification_token")
    private String verificationToken;
    // Thêm vào dưới các properties hiện tại trong User.java
    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "otp_expiry_time")
    private LocalDateTime otpExpiryTime;
    // Thêm vào trong class User.java
    @Column(name = "isDelete") // Tên cột trong DB của bạn viết thường (theo ảnh)
    private Integer isDelete;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}

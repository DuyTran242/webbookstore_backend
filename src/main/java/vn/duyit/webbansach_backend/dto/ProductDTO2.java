package vn.duyit.webbansach_backend.dto;

public class ProductDTO2 {
    private Long id;
    private String name;

    public ProductDTO2() {}

    public ProductDTO2(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
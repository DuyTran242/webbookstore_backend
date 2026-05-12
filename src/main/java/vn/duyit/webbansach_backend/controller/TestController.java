package vn.duyit.webbansach_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// Import Repository và Entity từ các package tương ứng của bạn
import vn.duyit.webbansach_backend.repository.ProductRepository;
import vn.duyit.webbansach_backend.entity.Product;

@RestController
@RequestMapping("/test1")
public class TestController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Product> getProducts() {
        return productRepository.findAll();
    }
}
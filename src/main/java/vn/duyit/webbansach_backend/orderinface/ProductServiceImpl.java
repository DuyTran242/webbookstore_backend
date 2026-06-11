package vn.duyit.webbansach_backend.orderinface;

import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.ProductDTO2;
import vn.duyit.webbansach_backend.entity.Product;
import vn.duyit.webbansach_backend.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService2 {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDTO2> getProductsWithSerial() {
        List<Product> products = productRepository.findByHasSerialTrue();

        // Chuyển đổi từ Product Entity sang Product DTO
        return products.stream()
                .map(product -> new ProductDTO2(product.getId(), product.getName()))
                .collect(Collectors.toList());
    }
}

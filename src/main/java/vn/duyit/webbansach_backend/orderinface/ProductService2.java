package vn.duyit.webbansach_backend.orderinface;

import vn.duyit.webbansach_backend.dto.ProductDTO2;

import java.util.List;
public interface ProductService2 {

    List<ProductDTO2> getProductsWithSerial();
}
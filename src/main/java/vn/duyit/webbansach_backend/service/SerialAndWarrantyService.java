package vn.duyit.webbansach_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.duyit.webbansach_backend.dto.*;
import vn.duyit.webbansach_backend.entity.*;
import vn.duyit.webbansach_backend.repository.*;

import java.time.LocalDateTime;

@Service
public class SerialAndWarrantyService {

    private final ProductSerialRepository serialRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public SerialAndWarrantyService(ProductSerialRepository sr, ProductRepository pr, OrderItemRepository oir) {
        this.serialRepository = sr;
        this.productRepository = pr;
        this.orderItemRepository = oir;
    }

    // 1. NHẬP KHO (INBOUND)
    @Transactional
    public void importSerials(Long productId, SerialInputDTO inputDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        for (String serial : inputDTO.getSerialNumbers()) {
            ProductSerial ps = new ProductSerial();
            ps.setProduct(product);
            ps.setSerialNumber(serial);
            ps.setStatus("IN_STOCK");
            serialRepository.save(ps);
        }

        // Cập nhật lại tổng tồn kho tự động
        int currentStock = serialRepository.countByProductIdAndStatus(productId, "IN_STOCK");
        product.setStockQuantity(currentStock);
        productRepository.save(product);
    }

    // 2. TRA CỨU BẢO HÀNH
    public WarrantyInfoDTO checkWarrantyBySerial(String serialNumber) {
        ProductSerial serial = serialRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại trong hệ thống"));

        if (!"SOLD".equals(serial.getStatus())) {
            throw new RuntimeException("Sản phẩm này chưa được bán ra!");
        }

        OrderItem item = orderItemRepository.findBySerialId(serial.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đơn hàng"));

        WarrantyInfoDTO dto = new WarrantyInfoDTO();
        dto.setProductName(item.getProduct().getName());
        dto.setSerialNumber(serialNumber);
        dto.setWarrantyEndDate(item.getWarrantyEndDate());
        dto.setOrderItemId(item.getId());

        // Cần join với bảng Order để lấy số điện thoại khách hàng (Giả định order có cột phone)
        dto.setCustomerPhone(item.getOrder().getPhone());

        if (item.getWarrantyEndDate().isAfter(LocalDateTime.now())) {
            dto.setWarrantyStatus("Còn bảo hành");
        } else {
            dto.setWarrantyStatus("Hết hạn");
        }
        return dto;
    }

    // 3. TRẢ HÀNG (RETURN)
    @Transactional
    public void processReturn(Long orderItemId, boolean isDefective) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Order Item"));

        ProductSerial serial = serialRepository.findById(item.getSerialId())
                .orElseThrow(() -> new RuntimeException("Lỗi dữ liệu IMEI"));

        // Thu hồi IMEI
        serial.setStatus(isDefective ? "DEFECTIVE" : "IN_STOCK");
        serialRepository.save(serial);

        // Hủy bảo hành
        item.setWarrantyStartDate(null);
        item.setWarrantyEndDate(null);
        orderItemRepository.save(item);

        // Logic hoàn tiền cho Order sẽ được gọi ở hàm khác...
    }
}
package vn.duyit.webbansach_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.service.SerialAndWarrantyService;
import vn.duyit.webbansach_backend.dto.*;

@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryController {

    private final SerialAndWarrantyService service;

    public InventoryController(SerialAndWarrantyService service) {
        this.service = service;
    }

    @PostMapping("/product/{id}/import-serials")
    public ResponseEntity<?> importSerials(@PathVariable Long id, @RequestBody SerialInputDTO input) {
        service.importSerials(id, input);
        return ResponseEntity.ok("Nhập kho IMEI thành công!");
    }

    @GetMapping("/warranty/check")
    public ResponseEntity<?> checkWarranty(@RequestParam String imei) {
        try {
            return ResponseEntity.ok(service.checkWarrantyBySerial(imei));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return/{orderItemId}")
    public ResponseEntity<?> returnProduct(@PathVariable Long orderItemId, @RequestParam boolean isDefective) {
        service.processReturn(orderItemId, isDefective);
        return ResponseEntity.ok("Xử lý trả hàng thành công!");
    }
}
package vn.duyit.webbansach_backend.controller;

import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.entity.Shipping;
import vn.duyit.webbansach_backend.service.ShippingService;

@RestController
@RequestMapping("/api/shipping")
@CrossOrigin
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping
    public Shipping shipping(@RequestBody Shipping shipping){
        return shippingService.saveShipping(shipping);
    }

}
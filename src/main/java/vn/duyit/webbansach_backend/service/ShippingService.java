package vn.duyit.webbansach_backend.service;


import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.entity.Shipping;
import vn.duyit.webbansach_backend.repository.ShippingRepository;

@Service
public class ShippingService {

    private final ShippingRepository shippingRepository;

    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    public Shipping saveShipping(Shipping shipping){
        return shippingRepository.save(shipping);
    }

}
package vn.duyit.webbansach_backend.controller;


import org.springframework.web.bind.annotation.*;
import vn.duyit.webbansach_backend.entity.Payment;
import vn.duyit.webbansach_backend.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment payment(@RequestBody Payment payment){
        return paymentService.savePayment(payment);
    }

}

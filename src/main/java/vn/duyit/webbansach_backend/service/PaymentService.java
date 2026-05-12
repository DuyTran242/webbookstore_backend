package vn.duyit.webbansach_backend.service;

import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.entity.Payment;
import vn.duyit.webbansach_backend.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }

}
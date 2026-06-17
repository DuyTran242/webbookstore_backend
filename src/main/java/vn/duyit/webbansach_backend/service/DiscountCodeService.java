package vn.duyit.webbansach_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.duyit.webbansach_backend.dto.DiscountCodeDTO;
import vn.duyit.webbansach_backend.entity.DiscountCode;
import vn.duyit.webbansach_backend.repository.DiscountCodeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountCodeService {

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    public List<DiscountCode> getAllDiscountCodes() {
        return discountCodeRepository.findAll();
    }

    public List<DiscountCode> getActiveDiscountCodes() {
        return discountCodeRepository.findByIsActiveTrue();
    }

    public DiscountCode getDiscountCodeById(Long id) {
        return discountCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá"));
    }

    public DiscountCode createDiscountCode(DiscountCodeDTO dto) {
        if (discountCodeRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Mã giảm giá đã tồn tại!");
        }
        DiscountCode code = new DiscountCode();
        code.setCode(dto.getCode());
        code.setDescription(dto.getDescription());
        code.setDiscountPercentage(dto.getDiscountPercentage());
        code.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        code.setMinOrderValue(dto.getMinOrderValue());
        code.setStartDate(dto.getStartDate());
        code.setEndDate(dto.getEndDate());
        code.setUsageLimit(dto.getUsageLimit());
        code.setIsActive(dto.getIsActive());
        code.setUsedCount(0);
        return discountCodeRepository.save(code);
    }

    public DiscountCode updateDiscountCode(Long id, DiscountCodeDTO dto) {
        DiscountCode code = getDiscountCodeById(id);
        
        Optional<DiscountCode> existing = discountCodeRepository.findByCode(dto.getCode());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Mã giảm giá này đã tồn tại!");
        }

        code.setCode(dto.getCode());
        code.setDescription(dto.getDescription());
        code.setDiscountPercentage(dto.getDiscountPercentage());
        code.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        code.setMinOrderValue(dto.getMinOrderValue());
        code.setStartDate(dto.getStartDate());
        code.setEndDate(dto.getEndDate());
        code.setUsageLimit(dto.getUsageLimit());
        code.setIsActive(dto.getIsActive());
        code.setUpdatedAt(LocalDateTime.now());
        
        return discountCodeRepository.save(code);
    }

    public void deleteDiscountCode(Long id) {
        discountCodeRepository.deleteById(id);
    }

    public BigDecimal calculateDiscount(String codeString, BigDecimal subtotal) {
        DiscountCode code = discountCodeRepository.findByCode(codeString)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không hợp lệ!"));

        if (!code.getIsActive()) {
            throw new RuntimeException("Mã giảm giá không còn hoạt động.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (code.getStartDate() != null && now.isBefore(code.getStartDate())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian áp dụng.");
        }
        if (code.getEndDate() != null && now.isAfter(code.getEndDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn.");
        }

        if (code.getUsageLimit() != null && code.getUsedCount() >= code.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng.");
        }

        if (code.getMinOrderValue() != null && subtotal.compareTo(code.getMinOrderValue()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã.");
        }

        // Tính số tiền giảm
        BigDecimal discountPercentage = BigDecimal.valueOf(code.getDiscountPercentage());
        BigDecimal discountAmount = subtotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        // Kiểm tra số tiền giảm tối đa
        if (code.getMaxDiscountAmount() != null && discountAmount.compareTo(code.getMaxDiscountAmount()) > 0) {
            discountAmount = code.getMaxDiscountAmount();
        }

        return discountAmount;
    }

    public void incrementUsedCount(String codeString) {
        discountCodeRepository.findByCode(codeString).ifPresent(code -> {
            code.setUsedCount(code.getUsedCount() + 1);
            discountCodeRepository.save(code);
        });
    }
}

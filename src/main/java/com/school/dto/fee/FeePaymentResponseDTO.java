package com.school.dto.fee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePaymentResponseDTO {

    private Long id;
    private Long schoolId;
    private Long studentFeeId;
    private Long studentId;
    private BigDecimal amount;
    private String paymentMode;
    private String transactionId;
    private String remarks;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

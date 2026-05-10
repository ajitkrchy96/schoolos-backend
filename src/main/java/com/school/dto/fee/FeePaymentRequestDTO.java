package com.school.dto.fee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePaymentRequestDTO {

    @NotNull(message = "School ID is required")
    private Long schoolId;

    @NotNull(message = "Student fee ID is required")
    private Long studentFeeId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Payment mode is required")
    private String paymentMode;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    private String remarks;
}

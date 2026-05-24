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
public class CreateAndPayFeeRequestDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Fee structure ID is required")
    private Long feeStructureId;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be a positive number")
    private BigDecimal paymentAmount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String remarks;
    private String transactionId;
}

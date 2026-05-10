package com.school.dto.fee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFeeResponseDTO {

    private Long id;
    private Long schoolId;
    private Long studentId;
    private String studentName;
    private Long feeStructureId;
    private String feeStructureName;
    private Long classId;
    private String className;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private String status;
    private LocalDate dueDate;
    private LocalDateTime lastPaymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.school.dto.fee;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeSummaryResponseDTO {

    private Long totalStudents;

    private Long paidStudents;

    private Long partialStudents;

    private Long pendingStudents;

    private BigDecimal totalCollected;

    private BigDecimal totalDue;
}
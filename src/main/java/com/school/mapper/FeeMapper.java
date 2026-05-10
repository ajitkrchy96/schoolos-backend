package com.school.mapper;

import com.school.dto.fee.*;
import com.school.model.*;

import org.springframework.stereotype.Component;

@Component
public class FeeMapper {

    // ============================
    // STUDENT FEE → RESPONSE DTO
    // ============================
    public StudentFeeResponseDTO toResponseDTO(StudentFee fee) {

        return StudentFeeResponseDTO.builder()
                .id(fee.getId())
                .studentId(fee.getStudent().getId())
                .studentName(fee.getStudent().getFirstName() + " " + fee.getStudent().getLastName())
                .feeStructureId(fee.getFeeStructure().getId())
                .feeStructureName(fee.getFeeStructure().getName())
                .totalAmount(fee.getTotalAmount())
                .paidAmount(fee.getPaidAmount())
                .dueAmount(fee.getDueAmount())
                .status(fee.getStatus().name())
                .dueDate(fee.getDueDate())
                .lastPaymentDate(fee.getLastPaymentDate())
                .build();
    }

    // ============================
    // PAYMENT → RESPONSE DTO
    // ============================
    public FeePaymentResponseDTO toPaymentDTO(FeePayment payment) {

        return FeePaymentResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMode(payment.getPaymentMode().name())
                .transactionId(payment.getTransactionId())
                .remarks(payment.getRemarks())
                .build();
    }
}
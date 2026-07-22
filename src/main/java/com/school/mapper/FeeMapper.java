package com.school.mapper;

import com.school.dto.fee.*;
import com.school.model.*;
import com.school.utilenum.FeeStatus;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Objects;

@Component
public class FeeMapper {

    // ============================
    // STUDENT FEE → RESPONSE DTO
    // ============================
    public StudentFeeResponseDTO toResponseDTO(StudentFee fee) {

        return StudentFeeResponseDTO.builder()
                .id(fee.getId())
                .schoolId(fee.getSchool().getId())
                .studentId(fee.getStudent().getId())
                .studentName(fee.getStudent().getFirstName() + " " + fee.getStudent().getLastName())
                .classId(fee.getStudent().getClassEntity().getId())
                .className(fee.getStudent().getClassEntity().getName())
                .feeStructureId(fee.getFeeStructure().getId())
                .feeStructureName(fee.getFeeStructure().getName())
                .totalAmount(fee.getTotalAmount())
                .paidAmount(fee.getPaidAmount())
                .dueAmount(fee.getDueAmount())
                .status(fee.getStatus())
                .dueDate(fee.getDueDate())
                .lastPaymentDate(fee.getLastPaymentDate())
                .build();
    }

    public StudentFeeResponseDTO toResponseDTO(Student student, StudentFee fee) {
        return toResponseDTO(student, fee, null);
    }

    public StudentFeeResponseDTO toResponseDTO(Student student, StudentFee fee, FeeStructure defaultFeeStructure) {
        if (fee == null) {
            StudentFeeResponseDTO.StudentFeeResponseDTOBuilder builder = StudentFeeResponseDTO.builder()
                    .id(null)
                    .schoolId(student.getSchool().getId())
                    .studentId(student.getId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .classId(student.getClassEntity() != null ? student.getClassEntity().getId() : null)
                    .className(student.getClassEntity() != null ? student.getClassEntity().getName() : null)
                    .feeStructureId(defaultFeeStructure != null ? defaultFeeStructure.getId() : null)
                    .feeStructureName(defaultFeeStructure != null ? defaultFeeStructure.getName() : null)
                    .totalAmount(defaultFeeStructure != null ? defaultFeeStructure.getAmount() : BigDecimal.ZERO)
                    .paidAmount(BigDecimal.ZERO)
                    .dueAmount(defaultFeeStructure != null ? defaultFeeStructure.getAmount() : BigDecimal.ZERO)
                    .status(FeeStatus.NOT_ASSIGNED)
                    .dueDate(null)
                    .lastPaymentDate(null);

            return builder.build();
        }

        return toResponseDTO(fee);
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
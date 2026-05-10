package com.school.service.impl;

import com.school.dto.fee.*;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.ValidationException;
import com.school.mapper.FeeMapper;
import com.school.model.*;
import com.school.repository.*;
import com.school.service.fee.FeeService;

import com.school.utilenum.FeeStatus;
import com.school.utilenum.PaymentMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeeServiceImpl implements FeeService {

    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final FeeMapper feeMapper;

    // ============================
    // CREATE STUDENT FEE
    // ============================
    @Override
    public StudentFeeResponseDTO createStudentFee(Long schoolId, StudentFeeRequestDTO dto) {

        validateSchool(schoolId);

        Student student = studentRepository.findByIdAndSchoolId(dto.getStudentId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

        FeeStructure structure = feeStructureRepository.findByIdAndSchoolId(dto.getFeeStructureId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure", "id", dto.getFeeStructureId()));

        // Prevent duplicate fee assignment
        if (studentFeeRepository.existsByStudentIdAndFeeStructureId(dto.getStudentId(), dto.getFeeStructureId())) {
            throw new ValidationException("fee", "Fee already assigned to this student");
        }

        StudentFee fee = new StudentFee();
        fee.setSchool(student.getSchool());
        fee.setStudent(student);
        fee.setFeeStructure(structure);

        fee.setTotalAmount(structure.getAmount());
        fee.setPaidAmount(BigDecimal.ZERO);
        fee.setDueAmount(structure.getAmount());
        fee.setStatus(FeeStatus.PENDING);

        fee.setDueDate(dto.getDueDate());

        StudentFee saved = studentFeeRepository.save(fee);

        return feeMapper.toResponseDTO(saved);
    }

    // ============================
    // GET STUDENT FEE
    // ============================
    @Override
    @Transactional(readOnly = true)
    public StudentFeeResponseDTO getStudentFee(Long schoolId, Long studentId) {

        StudentFee fee = studentFeeRepository.findByStudentIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentFee", "studentId", studentId));

        return feeMapper.toResponseDTO(fee);
    }

    // ============================
    // PAY FEE (CORE LOGIC)
    // ============================
    @Override
    public StudentFeeResponseDTO payFee(Long schoolId, Long studentFeeId, FeePaymentRequestDTO dto) {

        StudentFee fee = studentFeeRepository.findByIdAndSchoolId(
                studentFeeId, schoolId
        ).orElseThrow(() -> new ResourceNotFoundException("StudentFee", "id", studentFeeId));

        BigDecimal paymentAmount = dto.getAmount();

        // ❌ Prevent overpayment
        if (paymentAmount.compareTo(fee.getDueAmount()) > 0) {
            throw new ValidationException("amount", "Payment exceeds due amount");
        }

        // Apply payment (core logic)
        fee.applyPayment(paymentAmount);

        // Save payment record
        FeePayment payment = new FeePayment();
        payment.setSchool(fee.getSchool());
        payment.setStudentFee(fee);
        payment.setAmount(paymentAmount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMode(PaymentMode.valueOf(dto.getPaymentMode()));
        payment.setTransactionId(dto.getTransactionId());
        payment.setRemarks(dto.getRemarks());
        payment.setReceiptNumber(
                "RCPT-" + System.currentTimeMillis()
        );

        feePaymentRepository.save(payment);

        return feeMapper.toResponseDTO(fee);
    }

    // ============================
    // GET PENDING FEES
    // ============================
    @Override
    @Transactional(readOnly = true)
    public Page<StudentFeeResponseDTO> getPendingFees(Long schoolId, Pageable pageable) {

        Page<StudentFee> page = studentFeeRepository.findBySchoolIdAndStatus(
                schoolId,
                FeeStatus.PENDING,
                pageable
        );

        return page.map(feeMapper::toResponseDTO);
    }

    // ============================
    // PAYMENT HISTORY
    // ============================
    @Override
    @Transactional(readOnly = true)
    public List<FeePaymentResponseDTO> getPaymentHistory(Long schoolId, Long studentFeeId) {

        List<FeePayment> payments = feePaymentRepository
                .findByStudentFeeIdAndSchoolIdOrderByPaymentDateDesc(studentFeeId, schoolId);

        return payments.stream()
                .map(feeMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // HELPER
    // ============================
    private void validateSchool(Long schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
    }
}
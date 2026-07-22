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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Override
    @Transactional(readOnly = true)
    public Page<StudentFeeResponseDTO> getAllFees(
            Long schoolId,
            Long classId,
            Long sectionId,
            String search,
            Pageable pageable) {

        Page<Object[]> page = null;
        String isSearch = search != null && !search.trim().isEmpty() ? search.trim() : null;
        if (isSearch != null) {
            page = studentRepository.findStudentsWithFees(
                    schoolId,
                    classId,
                    sectionId,
                    isSearch,
                    pageable
            );
        }else {
            page = studentRepository.findStudentsWithFees(
                    schoolId,
                    classId,
                    sectionId,
                    pageable
            );
        }


        Set<Long> classIds = page.getContent().stream()
                .map(objects -> (Student) objects[0])
                .map(student -> student.getClassEntity() != null ? student.getClassEntity().getId() : null)
                .filter(id -> id != null)
                .collect(Collectors.toCollection(HashSet::new));

        Map<Long, FeeStructure> defaultStructures = getDefaultFeeStructureByClass(schoolId, classIds);

        return page.map(objects -> {
            Student student = (Student) objects[0];
            StudentFee fee = (StudentFee) objects[1];
            FeeStructure defaultStructure = student.getClassEntity() != null
                    ? defaultStructures.get(student.getClassEntity().getId())
                    : null;
            return feeMapper.toResponseDTO(student, fee, defaultStructure);
        });
    }

    @Override
    public StudentFeeResponseDTO createAndPayFee(Long schoolId, CreateAndPayFeeRequestDTO dto) {
        validateSchool(schoolId);

        Student student = studentRepository.findByIdAndSchoolId(dto.getStudentId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

        FeeStructure structure = feeStructureRepository.findByIdAndSchoolId(dto.getFeeStructureId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure", "id", dto.getFeeStructureId()));

        StudentFee fee = studentFeeRepository.findByStudentIdAndSchoolId(dto.getStudentId(), schoolId)
                .orElseGet(() -> {
                    StudentFee newFee = new StudentFee();
                    newFee.setSchool(student.getSchool());
                    newFee.setStudent(student);
                    newFee.setFeeStructure(structure);
                    newFee.setTotalAmount(structure.getAmount());
                    newFee.setPaidAmount(BigDecimal.ZERO);
                    newFee.setDueAmount(structure.getAmount());
                    newFee.setStatus(FeeStatus.PENDING);
                    newFee.setDueDate(null);
                    return newFee;
                });

        BigDecimal paymentAmount = dto.getPaymentAmount();
        if (paymentAmount.compareTo(fee.getDueAmount()) > 0) {
            throw new ValidationException("amount", "Payment exceeds due amount");
        }

        fee.applyPayment(paymentAmount);

        StudentFee savedFee = studentFeeRepository.save(fee);

        FeePayment payment = new FeePayment();
        payment.setSchool(savedFee.getSchool());
        payment.setStudentFee(savedFee);
        payment.setAmount(paymentAmount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMode(PaymentMode.valueOf(dto.getPaymentMethod()));
        payment.setTransactionId(dto.getTransactionId());
        payment.setRemarks(dto.getRemarks());
        payment.setReceiptNumber("RCPT-" + System.currentTimeMillis());

        feePaymentRepository.save(payment);

        return feeMapper.toResponseDTO(savedFee);
    }

    @Override
    @Transactional(readOnly = true)
    public FeeSummaryResponseDTO getFeeSummary(Long schoolId) {

        List<StudentFee> fees = studentFeeRepository.findAllBySchoolId(schoolId);

        long totalStudents = fees.size();

        long paidStudents = fees.stream()
                .filter(f -> "PAID".equals(f.getStatus().name()))
                .count();

        long partialStudents = fees.stream()
                .filter(f -> "PARTIAL".equals(f.getStatus().name()))
                .count();

        long pendingStudents = fees.stream()
                .filter(f -> "PENDING".equals(f.getStatus().name()))
                .count();

        BigDecimal totalCollected = fees.stream()
                .map(StudentFee::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDue = fees.stream()
                .map(StudentFee::getDueAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return FeeSummaryResponseDTO.builder()
                .totalStudents(totalStudents)
                .paidStudents(paidStudents)
                .partialStudents(partialStudents)
                .pendingStudents(pendingStudents)
                .totalCollected(totalCollected)
                .totalDue(totalDue)
                .build();
    }


    // ============================
    // HELPER
    // ============================
    private Map<Long, FeeStructure> getDefaultFeeStructureByClass(Long schoolId, Set<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return new HashMap<>();
        }

        List<FeeStructure> structures = feeStructureRepository.findBySchoolIdAndClassEntityIdIn(schoolId, classIds);
        Map<Long, FeeStructure> result = new HashMap<>();
        for (FeeStructure structure : structures) {
            Long classId = structure.getClassEntity() != null ? structure.getClassEntity().getId() : null;
            if (classId != null && !result.containsKey(classId)) {
                result.put(classId, structure);
            }
        }
        return result;
    }

    private void validateSchool(Long schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
    }
}
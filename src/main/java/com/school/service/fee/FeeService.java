package com.school.service.fee;

import com.school.dto.fee.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeeService {

    StudentFeeResponseDTO createStudentFee(Long schoolId, StudentFeeRequestDTO dto);

    StudentFeeResponseDTO createAndPayFee(Long schoolId, CreateAndPayFeeRequestDTO dto);

    StudentFeeResponseDTO getStudentFee(Long schoolId, Long studentId);

    StudentFeeResponseDTO payFee(Long schoolId, Long studentFeeId, FeePaymentRequestDTO dto);

    Page<StudentFeeResponseDTO> getPendingFees(Long schoolId, Pageable pageable);

    List<FeePaymentResponseDTO> getPaymentHistory(Long schoolId, Long studentFeeId);

    Page<StudentFeeResponseDTO> getAllFees(Long schoolId, Long classId, Long sectionId, String search, Pageable pageable);

    FeeSummaryResponseDTO getFeeSummary(Long schoolId);
}
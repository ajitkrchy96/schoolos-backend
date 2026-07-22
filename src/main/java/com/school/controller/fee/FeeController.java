package com.school.controller.fee;

import com.school.dto.fee.*;
import com.school.service.fee.FeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools/{schoolId}/fees")
@RequiredArgsConstructor
@Slf4j
public class FeeController {

    private final FeeService feeService;

    @GetMapping
    public ResponseEntity<Page<StudentFeeResponseDTO>> getAllFees(

            @PathVariable Long schoolId,
            @RequestParam(required = false)
            Long classId,
            @RequestParam(required = false)
            Long sectionId,
            @RequestParam(required = false)
            String search, Pageable pageable
    ) {

        log.info("Fetching all fees for school {} with classId: {}, sectionId: {}, search: {}", schoolId, classId, sectionId, search);
        return ResponseEntity.ok(
                feeService.getAllFees(schoolId, classId, sectionId, search, pageable)
        );
    }


    // ============================
    // CREATE STUDENT FEE
    // ============================
    @PostMapping("/student-fee")
    public ResponseEntity<StudentFeeResponseDTO> createStudentFee(
            @PathVariable Long schoolId,
            @Valid @RequestBody StudentFeeRequestDTO dto) {

        log.info("Creating fee for student {} in school {}", dto.getStudentId(), schoolId);

        StudentFeeResponseDTO response = feeService.createStudentFee(schoolId, dto);
        return ResponseEntity.ok(response);
    }

    // ============================
    // GET STUDENT FEEx`
    // ============================
    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentFeeResponseDTO> getStudentFee(
            @PathVariable Long schoolId,
            @PathVariable Long studentId) {

        log.info("Fetching fee for student {} in school {}", studentId, schoolId);

        return ResponseEntity.ok(feeService.getStudentFee(schoolId, studentId));
    }

    // ============================
    // PAY FEE
    // ============================
    @PostMapping("/pay/{studentFeeId}")
    public ResponseEntity<StudentFeeResponseDTO> payFee(
            @PathVariable Long schoolId,
            @PathVariable Long studentFeeId,
            @Valid @RequestBody FeePaymentRequestDTO dto) {

        log.info("Processing payment for studentFeeId {} in school {}", studentFeeId, schoolId);

        return ResponseEntity.ok(feeService.payFee(schoolId, studentFeeId, dto));
    }

    // ============================
    // CREATE AND PAY FEE
    // ============================
    @PostMapping("/create-and-pay")
    public ResponseEntity<StudentFeeResponseDTO> createAndPayFee(
            @PathVariable Long schoolId,
            @Valid @RequestBody CreateAndPayFeeRequestDTO dto) {

        log.info("Creating and paying fee for student {} in school {}", dto.getStudentId(), schoolId);

        return ResponseEntity.ok(feeService.createAndPayFee(schoolId, dto));
    }

    // ============================
    // GET PENDING FEES
    // ============================
    @GetMapping("/pending")
    public ResponseEntity<Page<StudentFeeResponseDTO>> getPendingFees(
            @PathVariable Long schoolId,
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("Fetching pending fees for school {}", schoolId);

        return ResponseEntity.ok(feeService.getPendingFees(schoolId, pageable));
    }

    // ============================
    // PAYMENT HISTORY
    // ============================
    @GetMapping("/payments/{studentFeeId}")
    public ResponseEntity<List<FeePaymentResponseDTO>> getPaymentHistory(
            @PathVariable Long schoolId,
            @PathVariable Long studentFeeId) {

        log.info("Fetching payment history for studentFeeId {} in school {}", studentFeeId, schoolId);

        return ResponseEntity.ok(feeService.getPaymentHistory(schoolId, studentFeeId));
    }
    // ============================
// FEES SUMMARY
// ============================
    @GetMapping("/summary")
    public ResponseEntity<FeeSummaryResponseDTO> getFeeSummary(
            @PathVariable Long schoolId) {

        log.info("Fetching fee summary for school {}", schoolId);

        return ResponseEntity.ok(
                feeService.getFeeSummary(schoolId)
        );
    }
}
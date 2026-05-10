package com.school.service.impl;

import com.school.dto.dashboard.DashboardResponseDTO;
import com.school.exception.ResourceNotFoundException;
import com.school.repository.*;
import com.school.service.dashboard.DashboardService;

import com.school.utilenum.FeeStatus;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final StudentAttendanceRepository attendanceRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final SchoolRepository schoolRepository;

    @Override
    public DashboardResponseDTO getDashboard(Long schoolId) {

        validateSchool(schoolId);

        // Students
        long totalStudents = studentRepository.countBySchoolIdAndStatus(schoolId, "ACTIVE");

        // Attendance
        LocalDate today = LocalDate.now();
        long present = attendanceRepository.countPresent(schoolId, today);
        long absent = attendanceRepository.countAbsent(schoolId, today);

        // Fees
        long pendingFees = studentFeeRepository.countBySchoolIdAndStatus(schoolId, FeeStatus.PENDING);
        double pendingAmount = studentFeeRepository.getTotalPendingAmount(schoolId);

        // Revenue
        double collectedAmount = feePaymentRepository.getTotalCollectedAmount(schoolId);

        return DashboardResponseDTO.builder()
                .totalStudents(totalStudents)
                .presentToday(present)
                .absentToday(absent)
                .pendingFeesCount(pendingFees)
                .totalPendingAmount(pendingAmount)
                .totalCollectedAmount(collectedAmount)
                .build();
    }

    private void validateSchool(Long schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
    }
}
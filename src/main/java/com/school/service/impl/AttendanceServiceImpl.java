package com.school.service.impl;

import com.school.dto.attendance.*;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.ValidationException;
import com.school.model.*;
import com.school.repository.*;
import com.school.service.attendance.AttendanceService;

import com.school.utilenum.AttendanceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final StudentAttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;

    // ============================
    // MARK ATTENDANCE (BULK)
    // ============================
    @Override
    public List<AttendanceResponseDTO> markAttendance(Long schoolId, List<AttendanceRequestDTO> dtoList) {

        validateSchool(schoolId);

        return dtoList.stream().map(dto -> {

            Student student = studentRepository.findByIdAndSchoolId(dto.getStudentId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

            // Check if already exists
            StudentAttendance attendance = attendanceRepository
                    .findByStudentIdAndDateAndSchoolId(
                            student.getId(),
                            LocalDate.now(),
                            schoolId
                    )
                    .orElseThrow(() -> new ValidationException("Attendance not marked"));

            //TODO need to check
            /*if (attendance.getStatus() != AttendanceStatus.ABSENT) {
                throw new ValidationException("Student is not absent today");
            }*/

            attendance.setSchool(student.getSchool());
            attendance.setStudent(student);
            attendance.setDate(dto.getDate());
            attendance.setStatus(dto.getStatus());

            StudentAttendance saved = attendanceRepository.save(attendance);

            return mapToDTO(saved);

        }).collect(Collectors.toList());
    }

    // ============================
    // GET BY DATE
    // ============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByDate(Long schoolId, LocalDate date) {

        return attendanceRepository.findBySchoolIdAndDate(schoolId, date)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // STUDENT HISTORY
    // ============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getStudentAttendance(Long schoolId, Long studentId) {

        return attendanceRepository.findByStudentIdAndSchoolIdOrderByDateDesc(studentId, schoolId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // DASHBOARD
    // ============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getAttendanceSummary(Long schoolId, LocalDate date) {

        long present = attendanceRepository.countPresent(schoolId, date);
        long absent = attendanceRepository.countAbsent(schoolId, date);

        return AttendanceSummaryDTO.builder()
                .presentCount(present)
                .absentCount(absent)
                .build();
    }

    // ============================
    // HELPER
    // ============================
    private void validateSchool(Long schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
    }

    private AttendanceResponseDTO mapToDTO(StudentAttendance attendance) {
        return AttendanceResponseDTO.builder()
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .build();
    }
}
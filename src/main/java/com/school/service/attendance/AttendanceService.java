package com.school.service.attendance;

import com.school.dto.attendance.*;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<AttendanceResponseDTO> markAttendance(Long schoolId, List<AttendanceRequestDTO> dtoList);

    List<AttendanceResponseDTO> getAttendanceByDate(Long schoolId, LocalDate date);

    List<AttendanceResponseDTO> getStudentAttendance(Long schoolId, Long studentId);

    AttendanceSummaryDTO getAttendanceSummary(Long schoolId, LocalDate date);
}
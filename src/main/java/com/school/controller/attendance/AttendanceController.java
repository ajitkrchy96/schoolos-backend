package com.school.controller.attendance;

import com.school.dto.attendance.*;
import com.school.service.attendance.AttendanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schools/{schoolId}/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    // ============================
    // MARK ATTENDANCE
    // ============================
    @PostMapping
    public ResponseEntity<List<AttendanceResponseDTO>> markAttendance(
            @PathVariable Long schoolId,
            @Valid @RequestBody List<AttendanceRequestDTO> dtoList) {

        log.info("Marking attendance for school {}", schoolId);

        return ResponseEntity.ok(attendanceService.markAttendance(schoolId, dtoList));
    }

    // ============================
    // GET BY DATE
    // ============================
    @GetMapping("/date")
    public ResponseEntity<List<AttendanceResponseDTO>> getByDate(
            @PathVariable Long schoolId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(attendanceService.getAttendanceByDate(schoolId, date));
    }

    // ============================
    // STUDENT HISTORY
    // ============================
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getStudentAttendance(
            @PathVariable Long schoolId,
            @PathVariable Long studentId) {

        return ResponseEntity.ok(attendanceService.getStudentAttendance(schoolId, studentId));
    }

    // ============================
    // DASHBOARD
    // ============================
    @GetMapping("/summary")
    public ResponseEntity<AttendanceSummaryDTO> getSummary(
            @PathVariable Long schoolId,
            @RequestParam LocalDate date) {

        return ResponseEntity.ok(attendanceService.getAttendanceSummary(schoolId, date));
    }
}
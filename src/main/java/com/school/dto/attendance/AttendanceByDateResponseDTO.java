package com.school.dto.attendance;

import com.school.utilenum.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AttendanceByDateResponseDTO {

    private Long attendanceId;

    private Long studentId;

    private String studentName;

    private String className;

    private LocalDate date;

    private AttendanceStatus status;
}
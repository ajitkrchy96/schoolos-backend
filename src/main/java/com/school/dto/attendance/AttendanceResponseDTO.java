package com.school.dto.attendance;

import com.school.utilenum.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AttendanceResponseDTO {

    private Long studentId;
    private String studentName;
    private LocalDate date;
    private AttendanceStatus status;
}
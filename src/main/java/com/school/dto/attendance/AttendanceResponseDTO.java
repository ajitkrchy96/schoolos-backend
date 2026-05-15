package com.school.dto.attendance;

import com.school.utilenum.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AttendanceResponseDTO {

/*    private Long studentId;
    private String studentName;
    private LocalDate date;
    private AttendanceStatus status;*/
    private Long id;

    private Long studentId;

    private String studentName;

    private String className;

    private LocalDate date;

    private AttendanceStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
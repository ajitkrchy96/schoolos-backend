package com.school.dto.attendance;

import com.school.utilenum.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceRequestDTO {

    @NotNull
    private Long studentId;

    @NotNull
    private LocalDate date;

    @NotNull
    private AttendanceStatus status;
}
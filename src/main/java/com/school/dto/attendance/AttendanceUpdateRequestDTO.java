package com.school.dto.attendance;

import com.school.utilenum.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttendanceUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private AttendanceStatus status;
}
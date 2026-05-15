package com.school.dto.student;

import com.school.utilenum.StudentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentStatusUpdateRequest {

@NotNull(message = "Status is required")
private StudentStatus status;

}

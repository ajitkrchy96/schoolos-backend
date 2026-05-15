package com.school.mapper;

import com.school.dto.attendance.AttendanceResponseDTO;
import com.school.model.StudentAttendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceResponseDTO toResponseDTO(StudentAttendance attendance) {

        if (attendance == null) {
            return null;
        }

        return AttendanceResponseDTO.builder().id(attendance.getId())

                .studentId(attendance.getStudent() != null ? attendance.getStudent().getId() : null)

                .studentName(attendance.getStudent() != null ? attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName() : null)

                .className(attendance.getStudent() != null && attendance.getStudent().getClassEntity() != null ? attendance.getStudent().getClassEntity().getName() : null)

                .date(attendance.getDate())

                .status(attendance.getStatus())

                .createdAt(attendance.getCreatedAt())

                .updatedAt(attendance.getUpdatedAt())

                .build();
    }
}
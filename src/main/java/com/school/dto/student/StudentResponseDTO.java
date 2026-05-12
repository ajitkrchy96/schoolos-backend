package com.school.dto.student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    StudentResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private String admissionNo;
    private String phone;
    private String status;
    private Long schoolId;
    private String schoolName;
    private Long classId;
    private String className;
    private Long sectionId;
    private String sectionName;
    private Long parentId;
    private String parentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String fatherName;
    private String motherName;
    private String parentPhone;
    private String parentEmail;
    private String parentAddress;

}

package com.school.mapper;

import com.school.dto.student.StudentRequestDTO;
import com.school.dto.student.StudentResponseDTO;
import com.school.model.ClassEntity;
import com.school.model.Parent;
import com.school.model.School;
import com.school.model.Section;
import com.school.model.Student;
import org.springframework.stereotype.Component;


@Component
public class StudentMapper {

    public Student toEntity(StudentRequestDTO dto, School school, Parent parent,
                           ClassEntity classEntity, Section section) {
        if (dto == null) {
            return null;
        }

        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDob(dto.getDob());
        student.setAdmissionNo(dto.getAdmissionNo());
        student.setPhone(dto.getPhone());
        student.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        student.setSchool(school);
            student.setParent(parent);
        student.setClassEntity(classEntity);
        student.setSection(section);

        return student;
    }

    public StudentResponseDTO toResponseDTO(Student student) {
        if (student == null) {
            return null;
        }

        return StudentResponseDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .gender(student.getGender())
                .dob(student.getDob())
                .admissionNo(student.getAdmissionNo())
                .phone(student.getPhone())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .schoolId(student.getSchool() != null ? student.getSchool().getId() : null)
                .schoolName(student.getSchool() != null ? student.getSchool().getName() : null)
                .classId(student.getClassEntity() != null ? student.getClassEntity().getId() : null)
                .className(student.getClassEntity() != null ? student.getClassEntity().getName() : null)
                .sectionId(student.getSection() != null ? student.getSection().getId() : null)
                .sectionName(student.getSection() != null ? student.getSection().getName() : null)
                .parentId(student.getParent() != null ? student.getParent().getId() : null)
                .parentName(student.getParent() != null ?
                        student.getParent().getFatherName() + " " + student.getParent().getMotherName() : null)
                .build();
    }

    public void updateEntity(StudentRequestDTO dto, Student student, ClassEntity classEntity, Section section, Parent parent) {
        if (dto == null) {
            return;
        }

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setGender(dto.getGender());
        student.setDob(dto.getDob());
        student.setAdmissionNo(dto.getAdmissionNo());
        student.setPhone(dto.getPhone());

        if (parent != null) {
            student.setParent(parent);
        }
        if (dto.getStatus() != null) {
            student.setStatus(dto.getStatus());
        }
        if (classEntity != null) {
            student.setClassEntity(classEntity);
        }
        if (section != null) {
            student.setSection(section);
        }
    }
}

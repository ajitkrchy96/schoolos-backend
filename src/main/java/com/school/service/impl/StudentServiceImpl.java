package com.school.service.impl;

import com.school.dto.student.StudentFilterDTO;
import com.school.dto.student.StudentRequestDTO;
import com.school.dto.student.StudentResponseDTO;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.UnauthorizedAccessException;
import com.school.exception.ValidationException;
import com.school.mapper.StudentMapper;
import com.school.model.ClassEntity;
import com.school.model.Parent;
import com.school.model.School;
import com.school.model.Section;
import com.school.model.Student;
import com.school.repository.ClassEntityRepository;
import com.school.repository.ParentRepository;
import com.school.repository.SchoolRepository;
import com.school.repository.SectionRepository;
import com.school.repository.StudentRepository;
import com.school.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final ParentRepository parentRepository;
    private final ClassEntityRepository classEntityRepository;
    private final SectionRepository sectionRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponseDTO createStudent(Long schoolId, StudentRequestDTO requestDTO) {
        log.info("Creating student for school ID: {}", schoolId);

        // Validate school ID in request matches path parameter
        if (!requestDTO.getSchoolId().equals(schoolId)) {
            throw new ValidationException("schoolId", "School ID in request does not match path parameter");
        }

        if (studentRepository.existsByAdmissionNoAndSchoolId(requestDTO.getAdmissionNo(), schoolId)) {
           throw new ValidationException("admissionNo", "Admission number already exists for this school");
        }

        // Validate school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId));

/*        // Validate parent exists and belongs to the school
        Parent parent = parentRepository.findByIdAndSchoolId(requestDTO.getParentId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", requestDTO.getParentId()));*/

        Parent parent = parentRepository
                .findByPhoneAndSchoolId(requestDTO.getParentPhone(), schoolId)
                .orElseGet(() -> {
                    Parent newParent = new Parent();
                    newParent.setSchool(school);
                    newParent.setFatherName(requestDTO.getFatherName());
                    newParent.setMotherName(requestDTO.getMotherName());
                    newParent.setPhone(requestDTO.getParentPhone());
                    newParent.setEmail(requestDTO.getParentEmail());
                    newParent.setAddress(requestDTO.getParentAddress());

                    return parentRepository.save(newParent);
                });

        // Validate class exists and belongs to the school
        ClassEntity classEntity = classEntityRepository.findByIdAndSchoolId(requestDTO.getClassId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("ClassEntity", "id", requestDTO.getClassId()));

        // Validate section exists and belongs to the school
        Section section = sectionRepository.findByIdAndSchoolId(requestDTO.getSectionId(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", requestDTO.getSectionId()));

        // Create and save student
        Student student = studentMapper.toEntity(requestDTO, school, parent, classEntity, section);
        Student savedStudent = studentRepository.save(student);

        log.info("Student created successfully with ID: {}", savedStudent.getId());
        return studentMapper.toResponseDTO(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long schoolId, Long studentId) {
        log.debug("Fetching student ID: {} for school ID: {}", studentId, schoolId);

        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(Long schoolId, StudentFilterDTO filterDTO, Pageable pageable) {
        log.debug("Fetching all students for school ID: {} with filter: {}", schoolId, filterDTO);

        // Validate school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }

        Page<Student> studentPage;

        // Apply search filter if provided
        if (filterDTO.getSearchTerm() != null && !filterDTO.getSearchTerm().trim().isEmpty()) {
            studentPage = studentRepository.searchBySchoolId(schoolId, filterDTO.getSearchTerm().trim(), pageable);
        }
        // Apply class and section filters
        else if (filterDTO.getClassId() != null || filterDTO.getSectionId() != null) {
            studentPage = studentRepository.findBySchoolIdWithFilters(
                    schoolId,
                    filterDTO.getClassId(),
                    filterDTO.getSectionId(),
                    pageable
            );
        }
        // Get all active students
        else {
            studentPage = studentRepository.findAllActiveBySchoolId(schoolId, pageable);
        }

        List<StudentResponseDTO> responseDTOs = studentPage.getContent()
                .stream()
                .map(studentMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDTOs, pageable, studentPage.getTotalElements());
    }

    @Override
    public StudentResponseDTO updateStudent(Long schoolId, Long studentId, StudentRequestDTO requestDTO) {
        log.info("Updating student ID: {} for school ID: {}", studentId, schoolId);

        // Validate school ID in request
        if (!requestDTO.getSchoolId().equals(schoolId)) {
            throw new ValidationException("schoolId", "School ID in request does not match path parameter");
        }

        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }

        // Fetch existing student
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Parent parent = parentRepository
                .findByPhoneAndSchoolId(requestDTO.getParentPhone(), schoolId)
                .orElseGet(() -> {
                    Parent newParent = new Parent();
                    newParent.setSchool(student.getSchool());
                    newParent.setFatherName(requestDTO.getFatherName());
                    newParent.setMotherName(requestDTO.getMotherName());
                    newParent.setPhone(requestDTO.getParentPhone());
                    newParent.setEmail(requestDTO.getParentEmail());
                    newParent.setAddress(requestDTO.getParentAddress());
                    return parentRepository.save(newParent);
                });
        student.setParent(parent);

        // Fetch updated relationships with school validation
        ClassEntity classEntity = null;
        if (!student.getClassEntity().getId().equals(requestDTO.getClassId())) {
            classEntity = classEntityRepository.findByIdAndSchoolId(requestDTO.getClassId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("ClassEntity", "id", requestDTO.getClassId()));
        }

        Section section = null;
        if (!student.getSection().getId().equals(requestDTO.getSectionId())) {
            section = sectionRepository.findByIdAndSchoolId(requestDTO.getSectionId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section", "id", requestDTO.getSectionId()));
        }

        // Update student
        studentMapper.updateEntity(requestDTO, student, classEntity, section, parent);
        Student updatedStudent = studentRepository.save(student);

        log.info("Student ID: {} updated successfully", studentId);
        return studentMapper.toResponseDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long schoolId, Long studentId) {
        log.info("Soft deleting student ID: {} for school ID: {}", studentId, schoolId);

        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        student.setStatus("INACTIVE");
        studentRepository.save(student);

        log.info("Student ID: {} soft deleted successfully", studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudents(Long schoolId, String searchTerm, Pageable pageable) {
        log.debug("Searching students in school ID: {} with term: {}", schoolId, searchTerm);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ValidationException("searchTerm", "Search term cannot be empty");
        }

        // Validate school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }

        Page<Student> studentPage = studentRepository.searchBySchoolId(schoolId, searchTerm.trim(), pageable);

        List<StudentResponseDTO> responseDTOs = studentPage.getContent()
                .stream()
                .map(studentMapper::toResponseDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(responseDTOs, pageable, studentPage.getTotalElements());
    }
}

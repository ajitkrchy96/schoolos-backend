package com.school.service.student;

import com.school.dto.student.StudentFilterDTO;
import com.school.dto.student.StudentRequestDTO;
import com.school.dto.student.StudentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    /**
     * Create a new student
     *
     * @param schoolId the school ID
     * @param requestDTO the student request DTO
     * @return the created student response DTO
     */
    StudentResponseDTO createStudent(Long schoolId, StudentRequestDTO requestDTO);

    /**
     * Get a student by ID
     *
     * @param schoolId the school ID
     * @param studentId the student ID
     * @return the student response DTO
     */
    StudentResponseDTO getStudentById(Long schoolId, Long studentId);

    /**
     * Get all students with pagination and filtering
     *
     * @param schoolId the school ID
     * @param filterDTO the filter criteria
     * @param pageable the pagination information
     * @return a page of student response DTOs
     */
    Page<StudentResponseDTO> getAllStudents(Long schoolId, StudentFilterDTO filterDTO, Pageable pageable);

    /**
     * Update a student
     *
     * @param schoolId the school ID
     * @param studentId the student ID
     * @param requestDTO the student request DTO
     * @return the updated student response DTO
     */
    StudentResponseDTO updateStudent(Long schoolId, Long studentId, StudentRequestDTO requestDTO);

    /**
     * Soft delete a student
     *
     * @param schoolId the school ID
     * @param studentId the student ID
     */
    void deleteStudent(Long schoolId, Long studentId);

    /**
     * Search students by term (name, admission number, phone)
     *
     * @param schoolId the school ID
     * @param searchTerm the search term
     * @param pageable the pagination information
     * @return a page of matching student response DTOs
     */
    Page<StudentResponseDTO> searchStudents(Long schoolId, String searchTerm, Pageable pageable);
}

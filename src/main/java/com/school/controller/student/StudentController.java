package com.school.controller.student;

import com.school.dto.student.StudentFilterDTO;
import com.school.dto.student.StudentRequestDTO;
import com.school.dto.student.StudentResponseDTO;
import com.school.dto.student.StudentStatusUpdateRequest;
import com.school.service.student.StudentService;
import com.school.utilenum.StudentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools/{schoolId}/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    /**
     * Create a new student
     *
     * @param schoolId the school ID
     * @param requestDTO the student request DTO
     * @return ResponseEntity with the created student and HTTP 201
     */
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @PathVariable Long schoolId,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        log.info("POST request to create student for school ID: {}", schoolId);
        StudentResponseDTO response = studentService.createStudent(schoolId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a student by ID
     *
     * @param schoolId the school ID
     * @param studentId the student ID
     * @return ResponseEntity with the student and HTTP 200
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @PathVariable Long schoolId,
            @PathVariable Long studentId) {
        log.info("GET request to fetch student ID: {} from school ID: {}", studentId, schoolId);
        StudentResponseDTO response = studentService.getStudentById(schoolId, studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all students with pagination and filtering
     *
     * @param schoolId the school ID
     * @param classId optional filter by class ID
     * @param sectionId optional filter by section ID
     * @param searchTerm optional search term
     * @param pageable pagination information
     * @return ResponseEntity with a page of students and HTTP 200
     */
    @GetMapping
    public ResponseEntity<Page<StudentResponseDTO>> getAllStudents(
            @PathVariable Long schoolId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) StudentStatus status,
            @PageableDefault(size = 20, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET request to fetch all students for school ID: {} with classId: {}, sectionId: {}, searchTerm: {} status : {} ",
                schoolId, classId, sectionId, searchTerm, status);

        StudentFilterDTO filterDTO = StudentFilterDTO.builder()
                .classId(classId)
                .sectionId(sectionId)
                .searchTerm(searchTerm)
                .status(status)
                .build();

        Page<StudentResponseDTO> response = studentService.getAllStudents(schoolId, filterDTO, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a student
     *
     * @param schoolId the school ID
     * @param studentId the student ID
     * @param requestDTO the student request DTO
     * @return ResponseEntity with the updated student and HTTP 200
     */
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long schoolId,
            @PathVariable Long studentId,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        log.info("PUT request to update student ID: {} for school ID: {}", studentId, schoolId);
        StudentResponseDTO response = studentService.updateStudent(schoolId, studentId, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft delete a student (set status to INACTIVE)
     *
     * @param schoolId the school ID
     * @param studentId the student ID
     * @return ResponseEntity with HTTP 204 (No Content)
     */
    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long schoolId,
            @PathVariable Long studentId) {
        log.info("DELETE request to soft delete student ID: {} from school ID: {}", studentId, schoolId);
        studentService.deleteStudent(schoolId, studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search students by term
     *
     * @param schoolId the school ID
     * @param searchTerm the search term (name, admission number, phone)
     * @param pageable pagination information
     * @return ResponseEntity with a page of matching students and HTTP 200
     */
    @GetMapping("/search")
    public ResponseEntity<Page<StudentResponseDTO>> searchStudents(
            @PathVariable Long schoolId,
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 20, page = 0, sort = "firstName", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("GET request to search students in school ID: {} with term: {}", schoolId, searchTerm);
        Page<StudentResponseDTO> response = studentService.searchStudents(schoolId, searchTerm, pageable);
        return ResponseEntity.ok(response);
    }

 /*   @GetMapping
    public ResponseEntity<Page<StudentResponseDTO>> getStudents(
            @PathVariable Long schoolId,@RequestParam(required = false) StudentStatus status, Pageable pageable ) {
        return ResponseEntity.ok(studentService.getStudents(schoolId,status,pageable));
    }*/

    @PatchMapping("/{studentId}/status")
    public ResponseEntity<StudentResponseDTO> updateStudentStatus(
            @PathVariable Long schoolId,
            @PathVariable Long studentId,
            @Valid @RequestBody StudentStatusUpdateRequest request) {
        return ResponseEntity.ok(studentService.updateStudentStatus(schoolId, studentId, request));
    }


}

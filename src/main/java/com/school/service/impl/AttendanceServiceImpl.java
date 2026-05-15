package com.school.service.impl;

import com.school.dto.attendance.*;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.ValidationException;
import com.school.mapper.AttendanceMapper;
import com.school.model.*;
import com.school.repository.*;
import com.school.service.attendance.AttendanceService;

import com.school.utilenum.AttendanceStatus;
import com.school.utilenum.StudentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final StudentAttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final AttendanceMapper attendanceMapper;

    // ============================
    // MARK ATTENDANCE (BULK)
    // ============================
    @Override
    public List<AttendanceResponseDTO> markAttendance(Long schoolId, List<AttendanceRequestDTO> dtoList) {

        validateSchool(schoolId);

        return dtoList.stream().map(dto -> {

            Student student = studentRepository.findByIdAndSchoolId(dto.getStudentId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));

            // Check if already exists
            StudentAttendance attendance = attendanceRepository
                    .findByStudentIdAndDateAndSchoolId(
                            student.getId(),
                            LocalDate.now(),
                            schoolId
                    )
                    .orElseThrow(() -> new ValidationException("Attendance not marked"));

            //TODO need to check
            /*if (attendance.getStatus() != AttendanceStatus.ABSENT) {
                throw new ValidationException("Student is not absent today");
            }*/

            attendance.setSchool(student.getSchool());
            attendance.setStudent(student);
            attendance.setDate(dto.getDate());
            attendance.setStatus(dto.getStatus());

            StudentAttendance saved = attendanceRepository.save(attendance);

            return mapToDTO(saved);

        }).collect(Collectors.toList());
    }

    // ============================
    // GET BY DATE
    // ============================
    /*@Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getAttendanceByDate(Long schoolId, LocalDate date) {

        return attendanceRepository.findBySchoolIdAndDate(schoolId, date)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }*/

    @Override
    public List<AttendanceByDateResponseDTO> getAttendanceByDate(
            Long schoolId,
            LocalDate date
    ) {

        List<Student> students =
                studentRepository
                        .findBySchoolIdAndStatusOrderByFirstNameAsc(
                                schoolId,
                                StudentStatus.ACTIVE
                        );

        List<StudentAttendance> attendanceList =
                attendanceRepository.findBySchoolIdAndDate(
                        schoolId,
                        date
                );

        Map<Long, StudentAttendance> attendanceMap =
                attendanceList.stream()
                        .collect(Collectors.toMap(
                                a -> a.getStudent().getId(),
                                a -> a
                        ));

        return students.stream()
                .map(student -> {

                    StudentAttendance attendance =
                            attendanceMap.get(student.getId());

                    return AttendanceByDateResponseDTO.builder()
                            .attendanceId(
                                    attendance != null
                                            ? attendance.getId()
                                            : null
                            )
                            .studentId(student.getId())
                            .studentName(
                                    student.getFirstName() +
                                            " " +
                                            student.getLastName()
                            )
                            .className(
                                    student.getClassEntity() != null
                                            ? student.getClassEntity().getName()
                                            : null
                            )
                            .date(date)
                            .status(
                                    attendance != null
                                            ? attendance.getStatus()
                                            : null
                            )
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional
    public AttendanceResponseDTO createAttendance(
            Long schoolId,
            AttendanceRequestDTO requestDTO
    ) {

        Student student = studentRepository
                .findByIdAndSchoolId(
                        requestDTO.getStudentId(),
                        schoolId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student",
                                "id",
                                requestDTO.getStudentId()
                        )
                );

        if (student.getStatus() != StudentStatus.ACTIVE) {

            throw new ValidationException(
                    "student",
                    "Only active students allowed"
            );
        }

        Optional<StudentAttendance> existing =
                attendanceRepository.findByStudentIdAndDate(
                        requestDTO.getStudentId(),
                        requestDTO.getDate()
                );

        if (existing.isPresent()) {

            throw new ValidationException(
                    "attendance",
                    "Attendance already exists"
            );
        }

        StudentAttendance attendance =
                new StudentAttendance();

        attendance.setSchool(student.getSchool());

        attendance.setStudent(student);

        attendance.setDate(requestDTO.getDate());

        attendance.setStatus(requestDTO.getStatus());

        StudentAttendance saved =
                attendanceRepository.save(attendance);

        return attendanceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public AttendanceResponseDTO updateAttendance(
            Long schoolId,
            Long attendanceId,
            AttendanceUpdateRequestDTO requestDTO
    ) {

        StudentAttendance attendance =
                attendanceRepository.findById(attendanceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance",
                                        "id",
                                        attendanceId
                                )
                        );

        if (!attendance.getSchool().getId().equals(schoolId)) {

            throw new ValidationException(
                    "schoolId",
                    "Attendance does not belong to school"
            );
        }

        attendance.setStatus(requestDTO.getStatus());

        StudentAttendance updated =
                attendanceRepository.save(attendance);

        return attendanceMapper.toResponseDTO(updated);
    }

    // ============================
    // STUDENT HISTORY
    // ============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDTO> getStudentAttendance(Long schoolId, Long studentId) {

        return attendanceRepository.findByStudentIdAndSchoolIdOrderByDateDesc(studentId, schoolId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ============================
    // DASHBOARD
    // ============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryDTO getAttendanceSummary(Long schoolId, LocalDate date) {

        long present = attendanceRepository.countPresent(schoolId, date);
        long absent = attendanceRepository.countAbsent(schoolId, date);

        return AttendanceSummaryDTO.builder()
                .presentCount(present)
                .absentCount(absent)
                .build();
    }

    // ============================
    // HELPER
    // ============================
    private void validateSchool(Long schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
    }

    private AttendanceResponseDTO mapToDTO(StudentAttendance attendance) {
        return AttendanceResponseDTO.builder()
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .build();
    }
}
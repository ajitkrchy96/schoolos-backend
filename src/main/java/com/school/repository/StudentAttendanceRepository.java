package com.school.repository;

import com.school.model.StudentAttendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {

    // ✅ Get attendance for student on a date
    Optional<StudentAttendance> findByStudentIdAndDateAndSchoolId(
            Long studentId,
            LocalDate date,
            Long schoolId
    );
/*
    // ✅ Get all attendance for a date (class-wise later filter in service)
    List<StudentAttendance> findBySchoolIdAndDate(
            Long schoolId,
            LocalDate date
    );*/

    // ✅ Get student history
    List<StudentAttendance> findByStudentIdAndSchoolIdOrderByDateDesc(
            Long studentId,
            Long schoolId
    );

    // ✅ Count present students (dashboard)
    @Query("""
        SELECT COUNT(a) FROM StudentAttendance a
        WHERE a.school.id = :schoolId
        AND a.date = :date
        AND a.status = 'PRESENT'
    """)
    long countPresent(@Param("schoolId") Long schoolId,
                      @Param("date") LocalDate date);

    // ✅ Count absent students
    @Query("""
        SELECT COUNT(a) FROM StudentAttendance a
        WHERE a.school.id = :schoolId
        AND a.date = :date
        AND a.status = 'ABSENT'
    """)
    long countAbsent(@Param("schoolId") Long schoolId,
                     @Param("date") LocalDate date);


    Optional<StudentAttendance> findByStudentIdAndDate(Long studentId, LocalDate date);

    @Query("""
                SELECT sa
                FROM StudentAttendance sa
                WHERE sa.school.id = :schoolId
                AND sa.date = :date
            """)
    List<StudentAttendance> findBySchoolIdAndDate(@Param("schoolId") Long schoolId, @Param("date") LocalDate date);
}
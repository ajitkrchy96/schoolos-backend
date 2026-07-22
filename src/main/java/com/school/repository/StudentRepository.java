package com.school.repository;

import com.school.model.Student;
import com.school.model.StudentFee;
import com.school.utilenum.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query("""
            SELECT s FROM Student s 
            WHERE s.school.id = :schoolId 
            AND s.id = :studentId 
            AND s.status = 'ACTIVE'
            """)
    java.util.Optional<Student> findByIdAndSchoolId(@Param("studentId") Long studentId,
                                                    @Param("schoolId") Long schoolId);

    Page<Student> findBySchoolId(Long schoolId, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND (:classId IS NULL OR s.classEntity.id = :classId) " +
            "AND (:sectionId IS NULL OR s.section.id = :sectionId) " +
            "AND (:status IS NULL OR s.status = :status)")
            Page<Student> findBySchoolIdWithFilters(@Param("schoolId") Long schoolId,
                                            @Param("classId") Long classId,
                                            @Param("sectionId") Long sectionId,
                                            @Param("status") StudentStatus status,
                                            Pageable pageable);

    @Query("""
            SELECT s FROM Student s 
            WHERE s.school.id = :schoolId 
            AND (
                                LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                                LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                                s.admissionNo LIKE CONCAT('%', :searchTerm, '%') OR
                                s.phone LIKE CONCAT('%', :searchTerm, '%')
            )
            """)
    Page<Student> searchBySchoolId(@Param("schoolId") Long schoolId,
                                   @Param("searchTerm") String searchTerm,
                                   Pageable pageable);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.school.id = :schoolId AND s.status = 'ACTIVE'")
    long countActiveBySchoolId(@Param("schoolId") Long schoolId);

    boolean existsByAdmissionNoAndSchoolId(String admissionNo, Long schoolId);

    @Query("""
            SELECT s FROM Student s
            LEFT JOIN FETCH s.classEntity
            LEFT JOIN FETCH s.section
            LEFT JOIN FETCH s.parent
            WHERE s.school.id = :schoolId 
            AND s.status = 'ACTIVE'
            """)
    List<Student> findAllWithRelations(Long schoolId);

    long countBySchoolIdAndStatus(Long schoolId, StudentStatus status);

    Long countBySchoolId(Long schoolId);

    Page<Student> findBySchoolIdAndStatus(
            Long schoolId,
            StudentStatus status,
            Pageable pageable
    );

    Page<Student> findBySchoolIdAndStatusAndFirstNameContainingIgnoreCase(
            Long schoolId,
            StudentStatus status,
            String firstName,
            Pageable pageable
    );

    @Query("""
            SELECT s FROM Student s 
            WHERE s.school.id = :schoolId 
            AND s.id = :studentId
            """)
    java.util.Optional<Student> findAllTypeOfStudentByIdAndSchoolId(@Param("studentId") Long studentId,
                                                    @Param("schoolId") Long schoolId);

    List<Student> findBySchoolIdAndStatusOrderByFirstNameAsc(Long schoolId, StudentStatus status);

    @Query("""
            SELECT s, sa
            FROM Student s
            LEFT JOIN StudentAttendance sa ON sa.student.id = s.id
                AND sa.date = :date
                AND sa.school.id = :schoolId
            WHERE s.school.id = :schoolId
                AND s.status = 'ACTIVE'
                AND (:classId IS NULL OR s.classEntity.id = :classId)
                AND (:sectionId IS NULL OR s.section.id = :sectionId)
                                AND (:searchPattern IS NULL OR (
                                        LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchPattern, '%')) OR
                                        LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchPattern, '%')) OR
                                        s.admissionNo LIKE CONCAT('%', :searchPattern, '%') OR
                                        s.phone LIKE CONCAT('%', :searchPattern, '%')
                                ))
            ORDER BY s.firstName ASC
            """)
    List<Object[]> findStudentAttendanceByDateAndFilters(@Param("schoolId") Long schoolId,
                                                        @Param("date") java.time.LocalDate date,
                                                        @Param("classId") Long classId,
                                                        @Param("sectionId") Long sectionId,
                                                         @Param("searchPattern") String searchPattern);

    @Query("""
            SELECT s, sa
            FROM Student s
            LEFT JOIN StudentAttendance sa ON sa.student.id = s.id
                AND sa.date = :date
                AND sa.school.id = :schoolId
            WHERE s.school.id = :schoolId
                AND s.status = 'ACTIVE'
                AND (:classId IS NULL OR s.classEntity.id = :classId)
                AND (:sectionId IS NULL OR s.section.id = :sectionId)
            ORDER BY s.firstName ASC
            """)
    List<Object[]> findStudentAttendanceByDateAndFilters(@Param("schoolId") Long schoolId,
                                                         @Param("date") java.time.LocalDate date,
                                                         @Param("classId") Long classId,
                                                         @Param("sectionId") Long sectionId
                                                       );

    @Query(value = """
            SELECT DISTINCT s, sf
            FROM Student s
            LEFT JOIN StudentFee sf ON sf.id = (
                SELECT MAX(sf2.id)
                FROM StudentFee sf2
                WHERE sf2.student.id = s.id
                  AND sf2.school.id = :schoolId
            )
            WHERE s.school.id = :schoolId
              AND s.status = 'ACTIVE'
              AND (:classId IS NULL OR s.classEntity.id = :classId)
              AND (:sectionId IS NULL OR s.section.id = :sectionId)
              AND (:search IS NULL OR (
                    LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    s.admissionNo LIKE CONCAT('%', :search, '%') OR
                    s.phone LIKE CONCAT('%', :search, '%')
              ))
            ORDER BY s.firstName ASC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT s.id)
            FROM Student s
            WHERE s.school.id = :schoolId
              AND s.status = 'ACTIVE'
              AND (:classId IS NULL OR s.classEntity.id = :classId)
              AND (:sectionId IS NULL OR s.section.id = :sectionId)
              AND (:search IS NULL OR (
                    LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    s.admissionNo LIKE CONCAT('%', :search, '%') OR
                    s.phone LIKE CONCAT('%', :search, '%')
              ))
            """
    )
    Page<Object[]> findStudentsWithFees(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("search") String search,
            Pageable pageable);


    @Query(value = """
            SELECT DISTINCT s, sf
            FROM Student s
            LEFT JOIN StudentFee sf ON sf.id = (
                SELECT MAX(sf2.id)
                FROM StudentFee sf2
                WHERE sf2.student.id = s.id
                  AND sf2.school.id = :schoolId
            )
            WHERE s.school.id = :schoolId
              AND s.status = 'ACTIVE'
              AND (:classId IS NULL OR s.classEntity.id = :classId)
              AND (:sectionId IS NULL OR s.section.id = :sectionId)
            ORDER BY s.firstName ASC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT s.id)
            FROM Student s
            WHERE s.school.id = :schoolId
              AND s.status = 'ACTIVE'
              AND (:classId IS NULL OR s.classEntity.id = :classId)
              AND (:sectionId IS NULL OR s.section.id = :sectionId)
            """
    )
    Page<Object[]> findStudentsWithFees(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            Pageable pageable);
}

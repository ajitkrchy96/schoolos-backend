package com.school.repository;

import com.school.model.StudentFee;
import com.school.utilenum.FeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {

    @Query("SELECT sf FROM StudentFee sf WHERE sf.id = :studentFeeId AND sf.school.id = :schoolId")
    Optional<StudentFee> findByIdAndSchoolId(@Param("studentFeeId") Long studentFeeId,
                                             @Param("schoolId") Long schoolId);

/*
    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.id = :studentId AND sf.school.id = :schoolId")
    List<StudentFee> findByStudentIdAndSchoolId(@Param("studentId") Long studentId,
                                                @Param("schoolId") Long schoolId);
*/

    @Query("SELECT sf FROM StudentFee sf WHERE sf.school.id = :schoolId " +
            "AND sf.status IN ('PENDING', 'PARTIAL')")
    List<StudentFee> findPendingFeesBySchoolId(@Param("schoolId") Long schoolId);

    Optional<StudentFee> findByIdAndSchoolIdAndStatus(
            Long id,
            Long schoolId,
            FeeStatus status
    );

    Optional<StudentFee> findByStudentIdAndSchoolId(
            Long studentId,
            Long schoolId
    );

    Page<StudentFee> findBySchoolIdAndStatus(
            Long schoolId,
            FeeStatus status,
            Pageable pageable
    );

    boolean existsByStudentIdAndFeeStructureId(
            Long studentId,
            Long feeStructureId
    );

    @Query("""
            SELECT COALESCE(SUM(f.dueAmount), 0)
            FROM StudentFee f
            WHERE f.school.id = :schoolId
            AND f.status != 'PAID'
            """)
    double getTotalPendingAmount(Long schoolId);

    long countBySchoolIdAndStatus(Long schoolId, FeeStatus status);

    @Query("""
            SELECT COALESCE(SUM(f.dueAmount), 0)
            FROM StudentFee f
            WHERE f.student.id = :studentId
            AND f.school.id = :schoolId
            """)
    Double getTotalDueAmount(Long studentId, Long schoolId);

    Page<StudentFee> findBySchoolId(Long schoolId, Pageable pageable);

    @Query("""
        SELECT sf
        FROM StudentFee sf
        JOIN sf.student s
        WHERE sf.school.id = :schoolId
          AND s.status = 'ACTIVE'
          AND (:classId IS NULL OR s.classEntity.id = :classId)
          AND (:sectionId IS NULL OR s.section.id = :sectionId)
        """)
    Page<StudentFee> findBySchoolIdAndAcademicFilters(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("search") String search,
            Pageable pageable);

    

    @Query("""
                SELECT sf
                FROM StudentFee sf
                WHERE sf.school.id = :schoolId
                AND (
                                        LOWER(sf.student.firstName) LIKE LOWER(:searchPattern) OR
                                        LOWER(sf.student.lastName) LIKE LOWER(:searchPattern) OR
                                        sf.student.admissionNo LIKE :searchPattern OR
                                        sf.student.phone LIKE :searchPattern
                )
            """)
    Page<StudentFee> findBySchoolIdAndStudentSearch(@Param("schoolId") Long schoolId, @Param("searchPattern") String searchPattern, Pageable pageable);

    List<StudentFee> findAllBySchoolId(Long schoolId);

}

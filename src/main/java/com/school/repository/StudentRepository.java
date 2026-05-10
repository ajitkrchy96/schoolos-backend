package com.school.repository;

import com.school.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
            SELECT s FROM Student s 
            WHERE s.school.id = :schoolId 
            AND s.id = :studentId 
            AND s.status = 'ACTIVE'
            """)
    java.util.Optional<Student> findByIdAndSchoolId(@Param("studentId") Long studentId,
                                                    @Param("schoolId") Long schoolId);

    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId AND s.status = 'ACTIVE' ")
    Page<Student> findAllActiveBySchoolId(@Param("schoolId") Long schoolId, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND (:classId IS NULL OR s.classEntity.id = :classId) " +
            "AND (:sectionId IS NULL OR s.section.id = :sectionId) " +
            "AND s.status = 'ACTIVE' ")
    Page<Student> findBySchoolIdWithFilters(@Param("schoolId") Long schoolId,
                                            @Param("classId") Long classId,
                                            @Param("sectionId") Long sectionId,
                                            Pageable pageable);
    // "AND (s.firstName LIKE %:searchTerm% OR s.lastName LIKE %:searchTerm% " +
/*    @Query("SELECT s FROM Student s WHERE s.school.id = :schoolId " +
            "AND (LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR s.admissionNo LIKE %:searchTerm% OR s.phone LIKE %:searchTerm%) " +
            "AND s.status = 'ACTIVE' " +
            "ORDER BY s.firstName ASC")*/
    @Query("""
            SELECT s FROM Student s 
            WHERE s.school.id = :schoolId 
            AND (
            LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(s.admissionNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(s.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            )
            AND s.status = 'ACTIVE'
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

    long countBySchoolIdAndStatus(Long schoolId, String status);
}

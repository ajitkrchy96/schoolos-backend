package com.school.repository;

import com.school.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    /*@Query("SELECT f FROM FeeStructure f WHERE f.id = :feeStructureId AND f.school.id = :schoolId")
    Optional<FeeStructure> findByIdAndSchoolId(@Param("feeStructureId") Long feeStructureId,
                                               @Param("schoolId") Long schoolId);*/

    Optional<FeeStructure> findByIdAndSchoolId(Long id, Long schoolId);


}

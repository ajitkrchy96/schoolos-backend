package com.school.repository;

import com.school.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {

    @Query("SELECT c FROM ClassEntity c WHERE c.id = :classId AND c.school.id = :schoolId")
    Optional<ClassEntity> findByIdAndSchoolId(@Param("classId") Long classId, @Param("schoolId") Long schoolId);
}

package com.school.repository;

import com.school.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("SELECT s FROM Section s WHERE s.id = :sectionId AND s.school.id = :schoolId")
    Optional<Section> findByIdAndSchoolId(@Param("sectionId") Long sectionId, @Param("schoolId") Long schoolId);
}

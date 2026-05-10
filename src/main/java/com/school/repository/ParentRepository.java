package com.school.repository;

import com.school.model.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

/*    @Query("SELECT p FROM Parent p WHERE p.id = :parentId AND p.school.id = :schoolId")
    Optional<Parent> findByIdAndSchoolId(@Param("parentId") Long parentId, @Param("schoolId") Long schoolId);

    @Query("SELECT p FROM Parent p WHERE p.phone = :phone AND p.school.id = :schoolId")
    Optional<Parent> findByPhoneAndSchoolId(@Param("phone") String phone, @Param("schoolId") Long schoolId);

    @Query("SELECT p FROM Parent p WHERE p.school.id = :schoolId")
    Page<Parent> findBySchoolId(@Param("schoolId") Long schoolId, Pageable pageable);*/

    Optional<Parent> findByIdAndSchoolId(Long parentId, Long schoolId);

    Optional<Parent> findByPhoneAndSchoolId(String phone, Long schoolId);

    Page<Parent> findBySchoolId(Long schoolId, Pageable pageable);
}

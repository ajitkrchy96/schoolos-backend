package com.school.repository;

import com.school.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findBySchoolId(Long schoolId);
    List<NotificationLog> findBySchoolIdOrderBySentAtDesc(Long schoolId);

    List<NotificationLog> findByStudentIdAndSchoolId(Long studentId, Long schoolId);
}
package com.school.repository;

import com.school.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

  //  @Query("SELECT COALESCE(SUM(f.amount), 0) - COALESCE(SUM(fp.amount), 0) FROM Fee f LEFT JOIN f.feePayments fp WHERE f.student.id = :studentId AND f.status = 'PENDING'")
    Double findDueAmountByStudentId(@Param("studentId") Long studentId);
}

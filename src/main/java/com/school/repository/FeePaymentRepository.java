package com.school.repository;

import com.school.model.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    @Query("SELECT fp FROM FeePayment fp WHERE fp.id = :paymentId AND fp.school.id = :schoolId")
    Optional<FeePayment> findByIdAndSchoolId(@Param("paymentId") Long paymentId,
                                             @Param("schoolId") Long schoolId);

    @Query("SELECT fp FROM FeePayment fp WHERE fp.studentFee.id = :studentFeeId AND fp.school.id = :schoolId ORDER BY fp.paymentDate DESC")
    List<FeePayment> findPaymentsByStudentFeeId(@Param("studentFeeId") Long studentFeeId,
                                                @Param("schoolId") Long schoolId);

    List<FeePayment> findByStudentFeeIdAndSchoolIdOrderByPaymentDateDesc(
            Long studentFeeId,
            Long schoolId
    );

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM FeePayment p
            WHERE p.school.id = :schoolId
            """)
    double getTotalCollectedAmount(Long schoolId);

    //Optional<FeePayment> findByIdAndSchoolId(Long paymentId, Long schoolId);
    @Query("""
            SELECT fp
            FROM FeePayment fp
            JOIN FETCH fp.studentFee sf
            JOIN FETCH sf.student s
            WHERE fp.id = :paymentId
            AND fp.school.id = :schoolId
            """)
    Optional<FeePayment> findReceiptDetails(Long paymentId, Long schoolId);

}
